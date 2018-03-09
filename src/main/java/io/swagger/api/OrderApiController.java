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
import java.util.Objects;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.CustomerOrderRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.threeten.bp.OffsetDateTime;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static com.disney.miguelmunoz.challenge.util.ResponseUtility.*;

//@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@SuppressWarnings({"HardcodedFileSeparator", "SimplifiableAnnotation", "CallToNumericToString"})
@Controller
public class OrderApiController implements OrderApi {

  private static final Logger log = LoggerFactory.getLogger(OrderApiController.class);

  public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private final ObjectMapper objectMapper;

  private CustomerOrderRepository customerOrderRepository;
  
  private MenuItemRepository menuItemRepository;
  
  private MenuItemOptionRepository menuItemOptionRepository;

  @Autowired
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
  @RequestMapping(value = "/order/addMenuItemOption/{order_id}/{menu_option_id}",
      produces = {"application/json"},
      method = RequestMethod.POST)
  public ResponseEntity<CreatedResponse> addMenuItemOptionToOrder(
      @PathVariable("order_id") final String orderId, 
      @PathVariable("menu_option_id")final String menuOptionId
  ) {
    return serve(HttpStatus.ACCEPTED, () -> {
      CustomerOrder order = customerOrderRepository.findOne(confirmAndDecodeInteger(orderId)); // throws ResponseException
      confirmNotNull(order, orderId);
      MenuItem item = order.getMenuItem();
      final Integer optionId = confirmAndDecodeInteger(menuOptionId);
      confirmItemHasOptionId(item, optionId); // throws ResponseException
      MenuItemOption option = menuItemOptionRepository.findOne(optionId); // throws ResponseException
      order.getOptions().add(option);
      CustomerOrder updatedOrder = customerOrderRepository.save(order);

      return buildCreatedResponseWithId(updatedOrder.getId().toString());
    });
  }

  private void confirmItemHasOptionId(final MenuItem item, final Integer menuOptionId) throws ResponseException {
    for (MenuItemOption option : item.getAllowedOptions()) {
      if (Objects.equals(option.getId(), menuOptionId)) {
        return;
      }
    }
    //noinspection ConstantConditions
    confirmNotNull(null, menuOptionId); // Error String is used in a unit test.
  }

  @Override
  @RequestMapping(value = "/order",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.PUT)
  public ResponseEntity<CreatedResponse> addOrder(CustomerOrderDto order) {
    return serveCreated(() -> {
      CustomerOrder orderEntity = makeCustomerOrder(order);
      confirmNull(orderEntity.getId());
      confirmNull(orderEntity.getCompleteTime());
      final MenuItem menuItem = orderEntity.getMenuItem();
      confirmNeverNull(menuItem);
      confirmEqual(Boolean.FALSE, orderEntity.getComplete());

      orderEntity.setOrderTime(new Date(System.currentTimeMillis()));
      final Date orderTime = orderEntity.getOrderTime();
      log.debug("Pojo set order time: {} = {}", orderTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(orderTime));
      // We don't use CascadeType.PERSIST, because it throws an exception if the menuItem already exists, claiming it's 
      // a detached object. So we save it first and re-add it to the entityOrder..
      MenuItem savedMenuItem = menuItemRepository.save(menuItem);
      orderEntity.setMenuItem(savedMenuItem);
      CustomerOrder savedOrder = customerOrderRepository.save(orderEntity);
      return buildCreatedResponseWithId(savedOrder.getId().toString());
    });
  }

  @Override
  @RequestMapping(value = "/order/complete/{id}",
      produces = {"application/json"},
      method = RequestMethod.POST)
  public ResponseEntity<CreatedResponse> completeOrder(@PathVariable("id") final String id) {
    return serve(HttpStatus.ACCEPTED, () -> {
      CustomerOrder order = customerOrderRepository.findOne(confirmAndDecodeInteger(id));
      confirmNotNull(order, id);
      confirmEqual("Already Complete", Boolean.FALSE, order.getComplete()); // Test searches for this String.
      order.setComplete(Boolean.TRUE);
      final Date completeTime = new Date(System.currentTimeMillis());
      log.debug("Pojo Complete Time: {} = {}", completeTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(completeTime));
      order.setCompleteTime(completeTime);
      Date cTime = order.getCompleteTime();
      log.debug("Pojo Complete Time: {} = {}", cTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(cTime));
      customerOrderRepository.save(order);
      return new CreatedResponse();
    });
  }

  @Override
  @RequestMapping(value = "/order/{id}",
      produces = {"application/json"},
      method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteOrder(@PathVariable("id") final String id) {
    return serve(HttpStatus.ACCEPTED, () -> {
      final Integer idInt = confirmAndDecodeInteger(id);
      customerOrderRepository.delete(idInt);
      return (Void) null;
    });
  }

  @Override
  @RequestMapping(value = "/order/search",
      produces = {"application/json"},
      method = RequestMethod.GET)
  public ResponseEntity<List<CustomerOrderDto>> searchByComplete(
      final String startingDate,
      final Boolean complete,
      final String endingDate
  ) {
    return serveOK(() -> {
      // Records whether it parsed both Date and Time or just Date.
      ReturnableReference<Boolean> usesTime = new ReturnableReference<>();
      Date startTime = parseDate(startingDate, null, usesTime); // throws ResponseException

      // works even if usesTime.getValue() is null. (Actually, I never expect it to be null.)
      //noinspection BooleanVariableAlwaysNegated
      @SuppressWarnings("EqualsReplaceableByObjectsCall")
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
      return convertCustomerOrderList(results);
    });
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
   * Parse a date. First tries DATE_TIME_FORMAT; then, if that fails, tries DATE_FORMAT.
   *
   * @param dateText The date as text
   * @param defaultDate The default Date to use if dateText is null or empty. If null, uses the value of getCurrentDate()
   * @param usesTime    Holds true if the date includes the time, false otherwise.
   * @return The Date specified in the DateText.
   * @throws ResponseException BAD_REQUEST if it's unable to parse the dateText.
   */
  private Date parseDate(String dateText, Date defaultDate, ReturnableReference<Boolean> usesTime) throws ResponseException {
    // I don't know if a missing date is sent as null or as an empty String, but this gets both cases.
    if ((dateText == null) || dateText.isEmpty()) {
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
    } catch (ParseException ex) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, String.format("Unable to parse: %s", dateText), ex);
    }
  }

  private static Date getCurrentDate() {
    GregorianCalendar now = new GregorianCalendar();
    GregorianCalendar today = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    return today.getTime();
  }

  @Override
  @RequestMapping(value = "/order/{id}",
      produces = {"application/json"},
      method = RequestMethod.GET)
  public ResponseEntity<CustomerOrderDto> searchForOrder(@PathVariable("id") final String id) {
    return serveOK(() -> {
      CustomerOrder order = customerOrderRepository.findOne(confirmAndDecodeInteger(id));
      SimpleDateFormat format = new SimpleDateFormat(PojoUtility.TIME_FORMAT);
      final Date orderTime = order.getOrderTime();
      if (orderTime != null) {
        log.info("Pojo Order time: {} = {}", orderTime, format.format(orderTime));
      }
      final Date completeTime = order.getCompleteTime();
      if (completeTime != null) {
        log.info("Pojo Compl time: {} = {}", completeTime, format.format(completeTime));
      }
      return objectMapper.convertValue(order, CustomerOrderDto.class);
    });
  }

  @Override
  @RequestMapping(value = "/order",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.POST)
  public ResponseEntity<CreatedResponse> updateOrder(final CustomerOrderDto order) {
    return null;
  }

  private CustomerOrder makeCustomerOrder(final CustomerOrderDto order) {
    return objectMapper.convertValue(order, CustomerOrder.class);
  }


  ////// Package-level methods for unit tests only! //////

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
