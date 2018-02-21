package io.swagger.api;

import java.sql.Date;
import java.util.List;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.repositories.CustomerOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.CustomerOrderDto;
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

  @Autowired
  private final ObjectMapper objectMapper;

//  private final HttpServletRequest request;

  @Autowired
  private CustomerOrderRepository customerOrderRepository;

  public OrderApiController(ObjectMapper objectMapper, CustomerOrderRepository repository) {
    this.objectMapper = objectMapper;
    this.customerOrderRepository = repository;
  }

  @Override
  public ResponseEntity<CreatedResponse> addOrder(CustomerOrderDto order) {
    CustomerOrder orderEntity = makeCustomerOrder(order);
    try {
      confirmNull(orderEntity.getId());
      confirmNull(orderEntity.getCompleteTime());
      confirmNotNull(orderEntity.getMenuItem());
      confirmEqual(Boolean.FALSE, orderEntity.getComplete());
      
      orderEntity.setOrderTime(new Date(System.currentTimeMillis()));
      CustomerOrder savedOrder = customerOrderRepository.save(orderEntity);
      return makeCreatedResponseWithId(savedOrder.getId().toString());
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  public ResponseEntity<Void> completeOrder(final String id) {
    try {
      CustomerOrder order = customerOrderRepository.findOne(decodeIdString(id));
      confirmNotNull(order);
      confirmEqual("Already Complete", Boolean.FALSE, order.getComplete());
      order.setComplete(true);
      order.setCompleteTime(new Date(System.currentTimeMillis()));
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (Exception e) {
      return makeVoidResponse(e);
    }
  }

  @Override
  public ResponseEntity<Void> deleteOrder(final String id) {
    return null;
  }

  @Override
  public ResponseEntity<CreatedResponse> searchByComplete(final OffsetDateTime startingDate, final Boolean complete, final OffsetDateTime endingDate) {
    return null;
  }

  @Override
  public ResponseEntity<CreatedResponse> searchForOrder(final String id) {
    try {
      CustomerOrder order = customerOrderRepository.findOne(decodeIdString(id));
      return makeStatusResponse(HttpStatus.FOUND, id);
    } catch (Exception e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  public ResponseEntity<Void> updateOrder(final CustomerOrderDto order) {
    return null;
  }

  private CustomerOrder makeCustomerOrder(final CustomerOrderDto order) {
    return objectMapper.convertValue(order, CustomerOrder.class);
  }


  ////// Package-leve methods for unit tests only! //////

  List<CustomerOrder> getAllTestOnly() {
    return customerOrderRepository.findAll();
  }
}
