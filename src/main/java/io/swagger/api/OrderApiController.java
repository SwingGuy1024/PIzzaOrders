package io.swagger.api;

import io.swagger.model.CustomerOrderDto;
import org.threeten.bp.OffsetDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Controller
public class OrderApiController implements OrderApi {

    private static final Logger log = LoggerFactory.getLogger(OrderApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public OrderApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> addOrder(@ApiParam(value = "The contents of the order" ,required=true )  @Valid @RequestBody CustomerOrderDto order) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> completeOrder(@ApiParam(value = "The id of the completed order",required=true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> deleteOrder(@ApiParam(value = "The id of the order to delete. Note that this does not mark it complete. Completed orders should not be deleted, but should be marked complete at /order/complete/.",required=true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> searchByComplete(@NotNull @ApiParam(value = "starting date of the order to search for, inclusive, or the date, if no ending date is specified. Format: yyyy-MM-dd ", required = true) @Valid @RequestParam(value = "startingDate", required = true) OffsetDateTime startingDate,@ApiParam(value = "If true, search for compete orders. If false, search for incomplete orders. If missing, returns both incomplete and complete in the date range.") @Valid @RequestParam(value = "complete", required = false) Boolean complete,@ApiParam(value = "Ending date of order to search for, inclusive. If missing, the starting date is used. Format: yyyy-MM-dd ") @Valid @RequestParam(value = "endingDate", required = false) OffsetDateTime endingDate) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> searchForOrder(@ApiParam(value = "id of the order to search for",required=true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> updateOrder(@ApiParam(value = "The contents of the order" ,required=true )  @Valid @RequestBody CustomerOrderDto order) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
