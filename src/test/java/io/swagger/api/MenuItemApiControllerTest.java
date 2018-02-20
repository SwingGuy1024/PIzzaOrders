package io.swagger.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * <p>Time: 1:44 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class MenuItemApiControllerTest {
  
  @Autowired
  private MenuItemApiController menuItemApiController;
  
  @Autowired
  private ObjectMapper objectMapper;
  
//  @Autowired
//  private MenuItemOptionRepository menuItemOptionRepository;

  @Test
  public void testBadInput() {
    MenuItemOptionDto menuItemOption = new MenuItemOptionDto();
    menuItemOption.setName("");
    menuItemOption.setDeltaPrice("5.00");

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Collections.singletonList(menuItemOption));
    menuItemDto.setName("BadItem");
    menuItemDto.setItemPrice("0.50");
    ResponseEntity<String> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }
  
  public void testGoodInput() {
    MenuItemOptionDto oliveOption = new MenuItemOptionDto();
    oliveOption.setName("olives");
    oliveOption.setDeltaPrice("0.30");
    
    MenuItemOptionDto pepOption = new MenuItemOptionDto();
    pepOption.setName("pepperoni");
    pepOption.setDeltaPrice("0.40");

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Arrays.asList(oliveOption, pepOption));
    menuItemDto.setName("GoodItem");
    menuItemDto.setItemPrice("0.50");
    ResponseEntity<String> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    String body = responseEntity.getBody();
    if (body.startsWith("id=")) {
      int id = Integer.valueOf(body.substring(3));
      MenuItem item = menuItemApiController.getMenuItemTestOnly(id);
      assertEquals("0.50", item.getItemPrice().toString());
      assertEquals("GoodItem", item.getName());
      Set<String> foodOptionSet = new HashSet<>();
      Collection<MenuItemOption> optionList = item.getMenuItemOptionList();
      for (MenuItemOption option: optionList) {
        foodOptionSet.add(option.getName());
      }
      assertThat(foodOptionSet, hasItems("olives", "pepperoni"));
      assertEquals(2, foodOptionSet.size());
    }
    
  }
}
