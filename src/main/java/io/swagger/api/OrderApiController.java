package io.swagger.api;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
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

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static com.disney.miguelmunoz.challenge.util.ResponseUtility.*;

//import java.util.Date;

//@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@SuppressWarnings({"HardcodedFileSeparator", "SimplifiableAnnotation", "CallToNumericToString"})
@Controller
public class OrderApiController implements OrderApi {

  private static final Logger log = LoggerFactory.getLogger(OrderApiController.class);

//	private static final long DAY_IN_SECONDS = 24L * 60L * 60L;
//	private static final long DAY_IN_MILLIS = DAY_IN_SECONDS * 1000L;
	private static final Duration ONE_DAY = Duration.ofDays(1L);
	private static final DateTimeFormatter DATE_TIME_LONG_FMT = PojoUtility.DATE_TIME_FMT;

	private final ObjectMapper objectMapper;

  private final CustomerOrderRepository customerOrderRepository;
  
  private final MenuItemRepository menuItemRepository;
  
  private final MenuItemOptionRepository menuItemOptionRepository;

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
  public ResponseEntity<CreatedResponse> addMenuItemOptionToCustomerOrder(
      @PathVariable("order_id") final Integer customerOrderId,
      @PathVariable("menu_option_id") final Integer menuOptionId
  ) {
    return serve(HttpStatus.ACCEPTED, () -> {
      CustomerOrder order = customerOrderRepository.findOne(customerOrderId); // throws ResponseException
      confirmFound(order, customerOrderId);
      MenuItem item = order.getMenuItem();
      confirmItemHasOptionId(item, menuOptionId); // throws ResponseException
      MenuItemOption option = menuItemOptionRepository.findOne(menuOptionId); // throws ResponseException
      order.getOptions().add(option);
      CustomerOrder updatedOrder = customerOrderRepository.save(order);

      return buildCreatedResponseWithId(updatedOrder.getId());
    });
  }

  private void confirmItemHasOptionId(final MenuItem item, final Integer menuOptionId) throws ResponseException {
    for (MenuItemOption option : item.getAllowedOptions()) {
      if (Objects.equals(option.getId(), menuOptionId)) {
        return;
      }
    }
    //noinspection ConstantConditions
    confirmFound(null, menuOptionId); // Error String is used in a unit test.
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

      orderEntity.setOrderTime(OffsetDateTime.now());
      final OffsetDateTime orderTime = orderEntity.getOrderTime();
      log.debug("About to format time of {}", orderTime.toString());
      log.debug("Pojo set order time: {} = {}", orderTime, DATE_TIME_LONG_FMT.format(orderTime));
      // We don't use CascadeType.PERSIST, because it throws an exception if the menuItem already exists, claiming it's 
      // a detached object. So we save it first and re-add it to the entityOrder..
      MenuItem savedMenuItem = menuItemRepository.save(menuItem);
      orderEntity.setMenuItem(savedMenuItem);
      CustomerOrder savedOrder = customerOrderRepository.save(orderEntity);
      return buildCreatedResponseWithId(savedOrder.getId());
    });
  }

  @Override
  @RequestMapping(value = "/order/complete/{id}",
      produces = {"application/json"},
      method = RequestMethod.POST)
  public ResponseEntity<CreatedResponse> completeOrder(@PathVariable("id") final Integer id) {
    return serve(HttpStatus.ACCEPTED, () -> {
      CustomerOrder order = customerOrderRepository.findOne(id);
      confirmFound(order, id);
      confirmEqual("Already Complete", Boolean.FALSE, order.getComplete()); // Test searches for this String.
      order.setComplete(Boolean.TRUE);
      final OffsetDateTime completeTime = OffsetDateTime.now();
      log.debug("Pojo Complete Time: {} = {}", completeTime, DATE_TIME_LONG_FMT.format(completeTime));
      order.setCompleteTime(completeTime);
      OffsetDateTime cTime = order.getCompleteTime();
      log.debug("Pojo Complete Time: {} = {}", cTime, DATE_TIME_LONG_FMT.format(cTime));
      customerOrderRepository.save(order);
      return new CreatedResponse();
    });
  }

  @Override
  @RequestMapping(value = "/order/{id}",
      produces = {"application/json"},
      method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteOrder(@PathVariable("id") final Integer id) {
    return serve(HttpStatus.ACCEPTED, () -> {
      confirmNeverNull(id);
      customerOrderRepository.delete(id);
      return null;
    });
  }

  @Override
  @RequestMapping(value = "/order/search",
      produces = {"application/json"},
      method = RequestMethod.GET)
  public ResponseEntity<List<CustomerOrderDto>> searchByComplete(
      final OffsetDateTime startingDate, 
      final Boolean complete, 
      final OffsetDateTime endingDate
  ) {
    return serveOK(() -> {
      // Records whether it parsed both Date and Time or just Date.

      final Collection<CustomerOrder> results;
      if (complete == null) {
        results = customerOrderRepository.findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTime(startingDate, endingDate);
      } else {
        results = customerOrderRepository.findByOrderTimeAfterAndOrderTimeBeforeAndCompleteOrderByOrderTime(startingDate, endingDate, complete);
      }
      return convertCustomerOrderList(results);
    });
  }

  private List<CustomerOrderDto> convertCustomerOrderList(final Collection<CustomerOrder> results) {
    List<CustomerOrderDto> dtoList = new LinkedList<>();
    for (CustomerOrder order : results) {
      CustomerOrderDto dto = new CustomerOrderDto();
      dto.setComplete(order.getComplete());
      final OffsetDateTime completeDateTime = order.getCompleteTime();
//      OffsetDateTime completeDateTime = objectMapper.convertValue(completeTime, OffsetDateTime.class);
      dto.setCompleteTime(completeDateTime);
      dto.setId(order.getId());
      OffsetDateTime orderDateTime = order.getOrderTime();
      dto.setOrderTime(orderDateTime);
      dto.setMenuItem(objectMapper.convertValue(order.getMenuItem(), MenuItemDto.class));
      dto.setOptions(convertList(order.getOptions()));
      dtoList.add(dto);
    }
    return dtoList;
  }


  @Override
  @RequestMapping(value = "/order/{id}",
      produces = {"application/json"},
      method = RequestMethod.GET)
  public ResponseEntity<CustomerOrderDto> searchForOrder(@PathVariable("id") final Integer id) {
    return serveOK(() -> {
      CustomerOrder order = customerOrderRepository.findOne(id);
//      SimpleDateFormat format = new SimpleDateFormat(PojoUtility.TIME_FORMAT);
      final OffsetDateTime orderTime = order.getOrderTime();
      if (orderTime != null) {
        log.info("Pojo Order time: {} = {}", orderTime, DATE_TIME_LONG_FMT.format(orderTime));
      }
      final OffsetDateTime completeTime = order.getCompleteTime();
      if (completeTime != null) {
        log.info("Pojo Compl time: {} = {}", completeTime, DATE_TIME_LONG_FMT.format(completeTime));
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
    return null; // Not yet written.
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
  
  Collection<CustomerOrder> findByOrderTimeTestOnly(OffsetDateTime findDate) {
    return customerOrderRepository.findByOrderTimeAfter(findDate);
  }
  
  CustomerOrder findByIdTestOnly(Integer id) {
    return customerOrderRepository.findOne(id);
  }
}
