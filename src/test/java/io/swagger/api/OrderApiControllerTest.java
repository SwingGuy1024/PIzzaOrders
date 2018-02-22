package io.swagger.api;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import io.swagger.model.CreatedResponse;
import io.swagger.model.CustomerOrderDto;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
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
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 5:37 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("CallToNumericToString")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class OrderApiControllerTest {
  private static final Logger log = LoggerFactory.getLogger(OrderApiControllerTest.class);
  
  @Autowired
  private MenuItemApiController menuItemApiController;
  
  @Autowired
  private OrderApiController orderApiController;
  
  
  @Test
  public void testAddNewOrder() {
    MenuItemDto pizzaMenuItemDto = MenuItemApiControllerTest.createPizzaMenuItem();
    menuItemApiController.addMenuItem(pizzaMenuItemDto);
    
    // Test of addOrder()

    CustomerOrderDto dto = new CustomerOrderDto();
    
    // bad values:
    dto.setId(5);
    dto.setCompleteTime(OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
    dto.setComplete(Boolean.TRUE);
    ResponseEntity<CreatedResponse> responseEntity = orderApiController.addOrder(dto);
    assertBad(responseEntity);

    dto.setId(null);
    responseEntity = orderApiController.addOrder(dto);
    assertBad(responseEntity);
    
    dto.setCompleteTime(null);
    responseEntity = orderApiController.addOrder(dto);
    assertBad(responseEntity);

    dto.setMenuItem(pizzaMenuItemDto);
    responseEntity = orderApiController.addOrder(dto);
    assertBad(responseEntity);

    dto.setComplete(Boolean.FALSE);
    responseEntity = orderApiController.addOrder(dto);
    assertCreated(responseEntity);
    String idString = responseEntity.getBody().getId();

    List<CustomerOrder> orders = orderApiController.getAllTestOnly();
    assertEquals(1, orders.size());
    CustomerOrder order = orders.get(0);
    assertEquals(Boolean.FALSE, order.getComplete());
    
    // Test of CompleteOrder

    // Test bad input (not found)    
    ResponseEntity<CreatedResponse> createResponse = orderApiController.completeOrder("10000");
    assertBad(createResponse);

    log.info("Completing order with id = {}", idString);
    createResponse = orderApiController.completeOrder(idString);
    assertEquals(HttpStatus.ACCEPTED, createResponse.getStatusCode());
    CustomerOrderDto body = orderApiController.searchForOrder(idString).getBody();
    log.info("DTO Order time: {}", body.getOrderTime());
    log.info("Dto Compl time: {}", body.getCompleteTime());
    assertNotNull(String.format("Not found at id %s", idString), body);
    log.info("Found DTO order with id = {}", body.getId());
    log.info("OrderDto: \n{}", body.toString());
    assertEquals(Boolean.TRUE, body.isComplete()); // Doesn't work yet.
    OffsetDateTime completeTime = body.getCompleteTime();
    GregorianCalendar now = new GregorianCalendar();
    assertThat(completeTime.getDayOfMonth() + 1, greaterThanOrEqualTo(now.get(GregorianCalendar.DAY_OF_MONTH)));
    
    // test of already complete
    createResponse = orderApiController.completeOrder(idString);
    assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
    assertThat(createResponse.getBody().getMessage(), containsString("Already Complete"));

    // Test of delete
    
    ResponseEntity<CreatedResponse> badDeleteResponse = orderApiController.deleteOrder("BAD_ID");
    assertBad(badDeleteResponse);
    
    ResponseEntity<CreatedResponse> deleteResponse = orderApiController.deleteOrder(idString);
    assertEquals(HttpStatus.ACCEPTED, deleteResponse.getStatusCode());
    
    // Make sure it got deleted.
    List<CustomerOrder> allOrders = orderApiController.getAllTestOnly();
    for (CustomerOrder customerOrder: allOrders) {
      assertNotEquals(idString, customerOrder.getId().toString());
    }
    
    
    
  }
  
  private void assertCreated(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.CREATED, responseEntity);
  }

  private void assertBad(final ResponseEntity<?> responseEntity) {
    assertStatus(HttpStatus.BAD_REQUEST, responseEntity);
  }
  
  private void assertStatus(HttpStatus status, final ResponseEntity<?> responseEntity) {
    assertEquals(String.format(
        "Request of %s", responseEntity.getStatusCode()),
        status,
        responseEntity.getStatusCode()
    );
  }
}
