package io.swagger.api;

import java.sql.Date;
import java.util.List;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.CustomerOrderRepository;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.model.CreatedResponse;
import io.swagger.model.CustomerOrderDto;
import io.swagger.model.MenuItemDto;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

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
  
  @Autowired
  private MenuItemApiController menuItemApiController;
  
  @Autowired
  private OrderApiController orderApiController;
  
  
  @Test
  public void testAddNewOrder() throws ResponseException {
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
    
    ResponseEntity<Void> voidResponse = orderApiController.completeOrder("10000");
    assertBad(voidResponse);

    voidResponse = orderApiController.completeOrder(idString);
    String body = orderApiController.searchForOrder(idString).getBody();
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
