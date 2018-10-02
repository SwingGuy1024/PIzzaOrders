package io.swagger.api;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.CustomerOrderDto;
import io.swagger.model.MenuItemDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 5:37 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"CallToNumericToString", "HardCodedStringLiteral"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class OrderApiControllerTest {
  private static final Logger log = LoggerFactory.getLogger(OrderApiControllerTest.class);

 	private static final Duration THREE_DAYS = Duration.ofDays(3L);

  @Autowired
  private MenuItemApiController menuItemApiController;
  
  @Autowired
  private OrderApiController orderApiController;

  @Autowired
  private ObjectMapper mapper;
  private static final Duration ONE_DAY = Duration.ofDays(1);
  private static final Duration ONE_HOUR = Duration.ofHours(1);

  @Test
  public void testAllOrderMethods() throws ResponseException {
    MenuItemDto pizzaMenuItemDto = MenuItemApiControllerTest.createPizzaMenuItem();
    ResponseEntity<CreatedResponse> menuResponse = menuItemApiController.addMenuItem(pizzaMenuItemDto);
    final String pizzaItemId = menuResponse.getBody().getId();
    assert pizzaItemId != null;
    
    // Test of addOrder()

    CustomerOrderDto customerOrderDto = new CustomerOrderDto();
    
    // bad values: All these values should be null.
    customerOrderDto.setId(5);
    customerOrderDto.setCompleteTime(OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
    customerOrderDto.setComplete(Boolean.TRUE);
    ResponseEntity<CreatedResponse> responseEntity = orderApiController.addOrder(customerOrderDto);
    assertBad(responseEntity);

    customerOrderDto.setId(null);
    responseEntity = orderApiController.addOrder(customerOrderDto);
    assertBad(responseEntity);
    
    customerOrderDto.setCompleteTime(null);
    responseEntity = orderApiController.addOrder(customerOrderDto);
    assertBad(responseEntity);

    customerOrderDto.setMenuItem(pizzaMenuItemDto);
    responseEntity = orderApiController.addOrder(customerOrderDto);
    assertBad(responseEntity);

    customerOrderDto.setComplete(Boolean.FALSE);
    responseEntity = orderApiController.addOrder(customerOrderDto);
    assertCreated(responseEntity);
    String idString = responseEntity.getBody().getId();

    List<CustomerOrder> orders = orderApiController.getAllTestOnly();
    assertEquals(1, orders.size());
    CustomerOrder order = orders.get(0);
    assertEquals(Boolean.FALSE, order.getComplete());
    
    // Test of CompleteOrder

    // Test bad input (not found)    
    ResponseEntity<CreatedResponse> createResponse = orderApiController.completeOrder("10000");
    assertNotFound(createResponse);

    log.info("Completing order with id = {}", idString);
    createResponse = orderApiController.completeOrder(idString);
    assertAccepted(createResponse);
    final ResponseEntity<CustomerOrderDto> customerOrderDtoResponseEntity = orderApiController.searchForOrder(idString);
    CustomerOrderDto body = customerOrderDtoResponseEntity.getBody();
    log.info("DTO Order time: {}", body.getOrderTime());
    log.info("Dto Compl time: {}", body.getCompleteTime());
    assertNotNull(String.format("Not found at id %s", idString), body);
    log.info("Found DTO order with id = {}", body.getId());
    //noinspection HardcodedLineSeparator
    log.info("OrderDto: \n{}", body.toString());
    assertEquals(Boolean.TRUE, body.isComplete()); // Doesn't work yet.
    OffsetDateTime completeTime = body.getCompleteTime();
    OffsetDateTime nowTime = OffsetDateTime.now();
    Duration duration = Duration.between(completeTime, nowTime);
    assertTrue(String.format("%s, !>= %s", completeTime, nowTime), nowTime.compareTo(completeTime) >= 0);

    // test of already complete
    createResponse = orderApiController.completeOrder(idString);
    assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
    assertThat(createResponse.getBody().getMessage(), containsString("Already Complete"));

    // Test of delete
    
    ResponseEntity<Void> badDeleteResponse = orderApiController.deleteOrder("BAD_ID");
    assertBad(badDeleteResponse);
    
    ResponseEntity<Void> deleteResponse = orderApiController.deleteOrder(idString);
    assertEquals(HttpStatus.ACCEPTED, deleteResponse.getStatusCode());
    
    // Make sure it got deleted.
    List<CustomerOrder> allOrders = orderApiController.getAllTestOnly();
    for (CustomerOrder customerOrder: allOrders) {
      assertNotEquals(idString, customerOrder.getId().toString());
    }
    
    // Test of searchByComplete

    MenuItem menuItem = menuItemApiController.getMenuItemTestOnly(PojoUtility.confirmAndDecodeInteger(pizzaItemId));    

    CustomerOrder orderM5Complete = makeOrder(-5, menuItem, true);
    CustomerOrder orderM4Complete = makeOrder(-4, menuItem, true);
    CustomerOrder orderM3Complete = makeOrder(-3, menuItem, true);
    CustomerOrder orderM2Complete = makeOrder(-2, menuItem, true);
    CustomerOrder orderM1Complete = makeOrder(-1, menuItem, true);
    CustomerOrder orderM3 = makeOrder(-3, menuItem, false);
    CustomerOrder orderM2 = makeOrder(-2, menuItem, false);
    CustomerOrder orderM1 = makeOrder(-1, menuItem, false);

    Collection<CustomerOrder> currentOrders = orderApiController.getAllTestOnly();
    log.info("Total of {} orders", currentOrders.size());
    for (CustomerOrder o : currentOrders) {
      log.info("id: {}, orderTime: {}, completeTime: {}", o.getId(), o.getOrderTime(), o.getCompleteTime());
    }

    Instant threeDaysAgo = Instant.now().minus(THREE_DAYS);
    Collection<CustomerOrder> timedOrder = orderApiController.findByOrderTimeTestOnly(threeDaysAgo);
    log.info("Search on 3 days ago produced {} results", timedOrder.size());
    for (CustomerOrder pastOrder : timedOrder) {
      log.info("id {} orderTime: {}", pastOrder.getId(), pastOrder.getOrderTime());
    }

//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    deltaDaysSearchTest(dateFormat, 8, 5, 3, -5);
    deltaDaysSearchTest(dateFormat, 7, 4, 3, -4);
    deltaDaysSearchTest(dateFormat, 6, 3, 3, -3);
    deltaDaysSearchTest(dateFormat, 4, 2, 2, -2);
    
//    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    deltaDaysSearchTest(dateTimeFormat, 8, 5, 3, -5);
//    deltaDaysSearchTest(dateTimeFormat, 7, 4, 3, -4);
//    deltaDaysSearchTest(dateTimeFormat, 6, 3, 3, -3);
//    deltaDaysSearchTest(dateTimeFormat, 4, 2, 2, -2);
    
    // test of addMenuItemOptionToOrder()
    
    Integer orderM5cId = orderM5Complete.getId();
    Collection<MenuItemOption> options = orderM5Complete.getMenuItem().getAllowedOptions();
    MenuItemOption option = options.iterator().next();
    final String menuOptionIdString = option.getId().toString();

    ResponseEntity<CreatedResponse> pizzaResponse = orderApiController.addMenuItemOptionToOrder("bad", menuOptionIdString);
    assertBad(pizzaResponse);
    pizzaResponse = orderApiController.addMenuItemOptionToOrder(orderM5cId.toString(), "bad");
    assertBad(pizzaResponse);
    pizzaResponse = orderApiController.addMenuItemOptionToOrder(orderM5cId.toString(), "10000");
    assertNotFound(pizzaResponse);
    assertThat(pizzaResponse.getBody().getMessage(), containsString("Missing object at id 10000"));
    pizzaResponse = orderApiController.addMenuItemOptionToOrder(orderM5cId.toString(), menuOptionIdString);
    assertStatus(HttpStatus.ACCEPTED, pizzaResponse);
    
    CustomerOrder revisedOrder = orderApiController.findByIdTestOnly(orderM5cId);
    MenuItemOption revisedOption = revisedOrder.getOptions().iterator().next();
    assertEquals(option.getId(), revisedOption.getId());
  }

  /**
   * test for search results starting at a different number of days before today. 
   * @param dateFormat The format used to parse data
   * @param fullCount expected count for all values of Completed
   * @param trueCount expect count for completed = true
   * @param falseCount expect count for completed = false
   * @param delta The number of days to go back for the starting point.
   */
  private void deltaDaysSearchTest(final DateTimeFormatter dateFormat, final int fullCount, final int trueCount, final int falseCount, final int delta) {
    final OffsetDateTime nowTime = OffsetDateTime.now();
    String nowTimeTxt = dateFormat.format(nowTime);

    // Search from 5 days earlier to now 
    final String startOfRange = delta(nowTime, delta, dateFormat);
    ResponseEntity<List<CustomerOrderDto>> dtoListResponse 
        = orderApiController.searchByComplete(startOfRange, null, nowTimeTxt);
    assertOkay(dtoListResponse);
    List<CustomerOrderDto> dtoList = dtoListResponse.getBody();
    assertNotNull(dtoList);
    assertEquals(fullCount, dtoList.size());
    
    log.info("Search Test({}, {}, {}, {}) produced {} results", fullCount, trueCount, falseCount, delta, dtoList.size());
    for (CustomerOrderDto dto : dtoList) {
      log.info("id {} startTime {}", dto.getId(), dto.getOrderTime());
    }

    dtoListResponse = orderApiController.searchByComplete(startOfRange, true, nowTimeTxt);
    dtoList = dtoListResponse.getBody();
    assertEquals(trueCount, dtoList.size());

    dtoListResponse = orderApiController.searchByComplete(startOfRange, false, nowTimeTxt);
    dtoList = dtoListResponse.getBody();
    assertEquals(falseCount, dtoList.size());
  }

  /**
   * Returns a time the specified number of days from the given date
   * @param date The given date
   * @param deltaDays The number of days to add or subtract
   * @param fmt The DateFormat instance to format the date
   * @return The resulting date, as a String.
   */
  private String delta(OffsetDateTime date, int deltaDays, DateTimeFormatter fmt) {
    return fmt.format(date.plus(deltaDays, ChronoUnit.DAYS));
  }
  
  private CustomerOrder makeOrder(int deltaDays, MenuItem menuItem, boolean complete) {
    CustomerOrder order = new CustomerOrder();
    order.setMenuItem(menuItem);
//    final long dateMillis = System.currentTimeMillis() + ((OrderApiController.DAY_IN_MILLIS * deltaDays) + 3600000); // add an hour
    Instant date = Instant.now().plus(deltaDays, ChronoUnit.DAYS).plus(ONE_HOUR);
    order.setOrderTime(date);
    if (complete) {
      Instant fiveDaysLater = date.plus(5, ChronoUnit.DAYS);
      order.setComplete(true);
      order.setCompleteTime(fiveDaysLater);
    }
    CustomerOrder savedOrder = orderApiController.saveOrderTestOnly(order);
    log.info("Created order id {}", savedOrder.getId());
    log.info("        {}", order);
    return order;
  }

  private void assertOkay(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.OK, responseEntity);
  }

  private void assertCreated(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.CREATED, responseEntity);
  }

  private void assertBad(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.BAD_REQUEST, responseEntity);
  }

  private void assertNotFound(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.NOT_FOUND, responseEntity);
  }
  
  private void assertAccepted(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.ACCEPTED, responseEntity);
  }

  private void assertStatus(HttpStatus status, final ResponseEntity<?> responseEntity) {
    assertEquals(String.format(
        "Request of %s", responseEntity.getStatusCode()),
        status,
        responseEntity.getStatusCode()
    );
  }
}
