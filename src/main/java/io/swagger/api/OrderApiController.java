package io.swagger.api;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.repositories.CustomerOrderRepository;
import com.disney.miguelmunoz.challenge.util.ResponseUtility;
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
      final Date orderTime = orderEntity.getOrderTime();
      log.debug("Pojo set order time: {} = {}", orderTime, new SimpleDateFormat(PojoUtility.TIME_FORMAT).format(orderTime));
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
  public ResponseEntity<CreatedResponse> searchByComplete(final OffsetDateTime startingDate, final Boolean complete, final OffsetDateTime endingDate) {
    return null;
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
}
