package io.swagger.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.CustomerOrderRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.disney.miguelmunoz.challenge.util.ResponseUtility;
import com.disney.miguelmunoz.challenge.util.ReturnableReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.CustomerOrderDto;
import io.swagger.model.MenuItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.threeten.bp.OffsetDateTime;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static com.disney.miguelmunoz.challenge.util.ResponseUtility.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Controller
public class OrderApiController implements OrderApi {

  private static final Logger log = LoggerFactory.getLogger(OrderApiController.class);

  public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired
  private final ObjectMapper objectMapper;

//  private final HttpServletRequest request;

  @Autowired
  private CustomerOrderRepository customerOrderRepository;
  
  @Autowired
  private MenuItemRepository menuItemRepository;
  
  @Autowired
  private MenuItemOptionRepository menuItemOptionRepository;

  public OrderApiController(
      ObjectMapper objectMapper, 
      MenuItemRepository menuItemRepository, 
      MenuItemOptionRepository menuItemOptionRepository, 
      CustomerOrderRepository repository
  ) {
    this.objectMapper = objectMapper;
    this.menuItemRepository = menuItemRepository;
    this.menuItemOptionRepository = menuItemOptionRepository;
    this.customerOrderRepository = repository;
  }

  @Override
  public ResponseEntity<CreatedResponse> addMenuItemOptionToOrder(final String orderId, final String menuOptionId) {
    try {
      CustomerOrder order = customerOrderRepository.findOne(decodeIdString(orderId)); // throws ResponseException
      MenuItem item = order.getMenuItem();
      final Integer optionId = decodeIdString(menuOptionId);
      confirmItemHasOptionId(item, optionId); // throws ResponseException
      MenuItemOption option = menuItemOptionRepository.findOne(optionId); // throws ResponseException
      order.getOptions().add(option);
      CustomerOrder updatedOrder = customerOrderRepository.save(order);
      
      return makeStatusResponseWithId(HttpStatus.ACCEPTED, updatedOrder.getId().toString());
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  private void confirmItemHasOptionId(final MenuItem item, final Integer menuOptionId) throws ResponseException {
    for (MenuItemOption option : item.getAllowedOptions()) {
      if (option.getId().equals(menuOptionId)) {
        return;
      }
    }
    throw new ResponseException(HttpStatus.BAD_REQUEST, "MenuItemOption not contained in Order's MenuItem"); // String is used in a unit test.
  }

  @Override
  public ResponseEntity<CreatedResponse> addOrder(CustomerOrderDto order) {
    CustomerOrder orderEntity = makeCustomerOrder(order);
    try {
      confirmNull(orderEntity.getId());
      confirmNull(orderEntity.getCompleteTime());
      final MenuItem menuItem = orderEntity.getMenuItem();
      confirmNotNull(menuItem);
      confirmEqual(Boolean.FALSE, orderEntity.getComplete());

      orderEntity.setOrderTime(new Date(System.currentTimeMillis()));
      final Date orderTime = orderEntity.getOrderTime();
      log.debug("Pojo set order time: {} = {}", orderTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(orderTime));
      // We don't use CascadeType.PERSIST, because it throws an exception if the menuItem already exists, claiming it's 
      // a detached object. So we save it first and re-add it to the entityOrder..
      MenuItem savedMenuItem = menuItemRepository.save(menuItem);
      orderEntity.setMenuItem(savedMenuItem);
      CustomerOrder savedOrder = customerOrderRepository.save(orderEntity);
      return makeCreatedResponseWithId(savedOrder.getId().toString());
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  public ResponseEntity<CreatedResponse> completeOrder(final String id) {
    try {
      CustomerOrder order = customerOrderRepository.findOne(decodeIdString(id));
      confirmNotNull(order);
      confirmEqual("Already Complete", Boolean.FALSE, order.getComplete()); // Test searches for this String.
      order.setComplete(Boolean.TRUE);
      final Date completeTime = new Date(System.currentTimeMillis());
      log.debug("Pojo Complete Time: {} = {}", completeTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(completeTime));
      order.setCompleteTime(completeTime);
      Date cTime = order.getCompleteTime();
      log.debug("Pojo Complete Time: {} = {}", cTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(cTime));
      customerOrderRepository.save(order);
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  public ResponseEntity<CreatedResponse> deleteOrder(final String id) {
    try {
      final Integer idInt = decodeIdString(id);
      customerOrderRepository.delete(idInt);
      return ResponseUtility.makeStatusResponse(HttpStatus.ACCEPTED);
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  public ResponseEntity<List<CustomerOrderDto>> searchByComplete(
      final String startingDate,
      final Boolean complete,
      final String endingDate
  ) {
    try {
      // Records whether it parsed both Date and Time or just Date.
      ReturnableReference<Boolean> usesTime = new ReturnableReference<>();
      Date startTime = parseDate(startingDate, null, usesTime); // throws ResponseException

      // works even if usesTime.getValue() is null. (Actually, I never expect it to be null.)
      boolean timeUsed = Boolean.TRUE.equals(usesTime.getValue());

      // startTime here is the default value. So if endingDate is empty or null, it uses the start date for both
      // ends of the range. For DateTime search this is useless, but for date-only searches, this limits the search
      // to a single day.
      Date endTime = parseDate(endingDate, startTime, usesTime); // throws ResponseException

      // If we only specified a Date for both figures...
      if (!timeUsed && !usesTime.getValue()) {
        endTime = new Date(endTime.getTime() + DAY_IN_MILLIS);
      }

      final Collection<CustomerOrder> results;
      if (complete == null) {
        results = customerOrderRepository.findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTime(startTime, endTime);
      } else {
        results = customerOrderRepository.findByOrderTimeAfterAndOrderTimeBeforeAndCompleteOrderByOrderTime(startTime, endTime, complete);
      }
      List<CustomerOrderDto> resultDtos = convertCustomerOrderList(results);
      return makeStatusResponse(HttpStatus.OK, resultDtos);
    } catch (Exception e) {
      return makeGenericErrorResponse(e);
    }
  }

  private List<CustomerOrderDto> convertCustomerOrderList(final Collection<CustomerOrder> results) {
    List<CustomerOrderDto> dtoList = new LinkedList<>();
    for (CustomerOrder order : results) {
      CustomerOrderDto dto = new CustomerOrderDto();
      dto.setComplete(order.getComplete());
      final Date completeTime = order.getCompleteTime();
      OffsetDateTime completeDateTime = objectMapper.convertValue(completeTime, OffsetDateTime.class);
      dto.setCompleteTime(completeDateTime);
      dto.setId(order.getId());
      OffsetDateTime orderDateTime = objectMapper.convertValue(order.getOrderTime(), OffsetDateTime.class);
      dto.setOrderTime(orderDateTime);
      dto.setMenuItem(objectMapper.convertValue(order.getMenuItem(), MenuItemDto.class));
      dto.setOptions(convertList(order.getOptions()));
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * Parse a date.
   *
   * @param dateText
   * @param defaultDate
   * @param usesTime    Holds true if the date includes the time, false otherwise.
   * @return
   * @throws ResponseException
   */
  private Date parseDate(String dateText, Date defaultDate, ReturnableReference<Boolean> usesTime) throws ResponseException {
    // I don't know if a missing date is sent as null or as an empty String, but this gets both cases.
    if (dateText == null || dateText.isEmpty()) {
      usesTime.setValue(Boolean.FALSE);
      if (defaultDate == null) {
        defaultDate = getCurrentDate();
      }
      return defaultDate;
    }
    try {
      usesTime.setValue(Boolean.TRUE);
      return DATE_TIME_FORMAT.parse(dateText);
    } catch (ParseException ignored) { }
    try {
      usesTime.setValue(Boolean.FALSE);
      return DATE_FORMAT.parse(dateText);
    } catch (ParseException ignored) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, String.format("Unable to parse: %s", dateText), ignored);
    }
  }

  private static Date getCurrentDate() {
    GregorianCalendar now = new GregorianCalendar();
    GregorianCalendar today = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    return today.getTime();
  }

  @Override
  public ResponseEntity<CustomerOrderDto> searchForOrder(final String id) {
    try {
      CustomerOrder order = customerOrderRepository.findOne(decodeIdString(id));
      SimpleDateFormat format = new SimpleDateFormat(PojoUtility.TIME_FORMAT);
      final Date orderTime = order.getOrderTime();
      if (orderTime != null) {
        log.info("Pojo Order time: {} = {}", orderTime, format.format(orderTime));
      }
      final Date completeTime = order.getCompleteTime();
      if (completeTime != null) {
        log.info("Pojo Compl time: {} = {}", completeTime, format.format(completeTime));
      }
      CustomerOrderDto dto = objectMapper.convertValue(order, CustomerOrderDto.class);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    } catch (Exception e) {
      log.debug(HttpStatus.BAD_REQUEST.toString(), e);
      return ResponseUtility.makeStatusResponse(HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<CreatedResponse> updateOrder(final CustomerOrderDto order) {
    return null;
  }

  private CustomerOrder makeCustomerOrder(final CustomerOrderDto order) {
    return objectMapper.convertValue(order, CustomerOrder.class);
  }


  ////// Package-leve methods for unit tests only! //////

  List<CustomerOrder> getAllTestOnly() {
    return customerOrderRepository.findAll();
  }
  
  // Used to save orders with different start dates for testing.
  CustomerOrder saveOrderTestOnly(CustomerOrder order) {
    return customerOrderRepository.save(order);
  }
  
  Collection<CustomerOrder> findByOrderTimeTestOnly(Date findDate) {
    return customerOrderRepository.findByOrderTimeAfter(findDate);
  }
  
  CustomerOrder findByIdTestOnly(Integer id) {
    return customerOrderRepository.findOne(id);
  }
}
