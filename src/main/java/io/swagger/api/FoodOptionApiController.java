package io.swagger.api;

import com.disney.miguelmunoz.challenge.entities.FoodOption;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.FoodOptionRepository;
import com.disney.miguelmunoz.challenge.util.ResponseUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Controller
public class FoodOptionApiController implements FoodOptionApi {

  private static final Logger log = LoggerFactory.getLogger(FoodOptionApiController.class);

  private final ObjectMapper objectMapper;

  @Autowired
  private final FoodOptionRepository foodOptionRepository;

  @org.springframework.beans.factory.annotation.Autowired
  public FoodOptionApiController(ObjectMapper objectMapper, FoodOptionRepository foodOptionRepository) {
    this.objectMapper = objectMapper;
    this.foodOptionRepository = foodOptionRepository;
  }

  public ResponseEntity<Void> addFoodOption(@NotNull @Valid @RequestParam(value = "name", required = true) String name) {
    try {
      FoodOption foodOption = new FoodOption();
      foodOption.setName(notEmpty(name)); // throws ResponseException
      foodOptionRepository.save(foodOption);
      return new ResponseEntity<Void>(HttpStatus.CREATED);
    } catch (ResponseException e) {
      return ResponseUtility.makeResponse(e);
    }
  }

}
