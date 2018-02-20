package io.swagger.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.FoodOption;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.Swagger2SpringBoot;
import io.swagger.model.FoodOptionDto;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 3:44 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class FoodOptionApiControllerTest {
  @Autowired private FoodOptionApiController foodOptionApiController;
  
  @Test
  public void testAllInput() {
    String name = null;
    
    ResponseEntity<Void> nullResponse = foodOptionApiController.addFoodOption(name);
    assertEquals(HttpStatus.BAD_REQUEST, nullResponse.getStatusCode());

    name = "";
    ResponseEntity<Void> emptyResponse = foodOptionApiController.addFoodOption(name);
    assertEquals(HttpStatus.BAD_REQUEST, emptyResponse.getStatusCode());
    
    name = "bananas";
    ResponseEntity<Void> fullResponse = foodOptionApiController.addFoodOption(name);
    assertEquals(HttpStatus.CREATED, fullResponse.getStatusCode());

    List<FoodOption> all = foodOptionApiController.getAllTestOnly();
    assertEquals(1, all.size());
    assertEquals("bananas", all.get(0).getName());
  }
}
