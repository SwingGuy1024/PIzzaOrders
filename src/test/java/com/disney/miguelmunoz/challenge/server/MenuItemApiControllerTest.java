package com.disney.miguelmunoz.challenge.server;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.disney.miguelmunoz.challenge.Application;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.framework.exception.BadRequest400Exception;
import com.disney.miguelmunoz.framework.exception.NotFound404Exception;
import com.disney.miguelmunoz.framework.exception.ResponseException;
import io.swagger.model.CreatedResponse;
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
@SuppressWarnings("MagicNumber")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class MenuItemApiControllerTest {
  
  @Autowired
  private MenuItemApiController menuItemApiController;
  
  // Tests of addMenuItem()
  
  @Test
  public void testAddMenuItemBadInput() {
    MenuItemOptionDto menuItemOption = new MenuItemOptionDto();
    menuItemOption.setName("");
    menuItemOption.setDeltaPrice(new BigDecimal("5.00"));

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Collections.singletonList(menuItemOption));
    menuItemDto.setName("BadItem");
    menuItemDto.setItemPrice("0.50");
	  try {
		  ResponseEntity<CreatedResponse> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
		  fail(responseEntity.toString());
    } catch (BadRequest400Exception ignored) { }
  }
  
  @Test
  public void testAddMenuItemGoodInput() throws ResponseException {
    MenuItemOptionDto oliveOption = makeMenuItemOptionDto("olives", "0.30");
    MenuItemOptionDto pepOption = makeMenuItemOptionDto("pepperoni", "0.40");

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Arrays.asList(oliveOption, pepOption));
    menuItemDto.setName("GoodItem");
    menuItemDto.setItemPrice("0.50");
    ResponseEntity<CreatedResponse> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    CreatedResponse body = responseEntity.getBody();
    Integer id = body.getId();
    if (id != null) {
      MenuItem item = menuItemApiController.getMenuItemTestOnly(id);
      assertEquals("0.50", item.getItemPrice().toString());
      assertEquals("GoodItem", item.getName());
      Set<String> foodOptionSet = new HashSet<>();
      Collection<MenuItemOption> optionList = item.getAllowedOptions();
      for (MenuItemOption option: optionList) {
        foodOptionSet.add(option.getName());
      }
      assertThat(foodOptionSet, hasItems("olives", "pepperoni"));
      assertEquals(2, foodOptionSet.size());
    }
    
  }

  private static MenuItemOptionDto makeMenuItemOptionDto(String name, String price) {
    MenuItemOptionDto option = new MenuItemOptionDto();
    option.setName(name);
    option.setDeltaPrice(new BigDecimal(price));
    return option;
  }

  // Tests of addMenuItemOption()
  
  @Test
  public void testAddOptionBadInput() {
    isNotFound(5, makeMenuItemOptionDto("olives","1000.00"));
    isNotFound(6, makeMenuItemOptionDto("pepperoni", "100.00"));
    isBadRequest(5, makeMenuItemOptionDto("","0.40"));
    isBadRequest(6, makeMenuItemOptionDto("", "0.50"));
  }

  private void isBadRequest(int id, MenuItemOptionDto optionDto) {
    try {
      final ResponseEntity<CreatedResponse> stringResponseEntity = menuItemApiController.addMenuItemOption(id, optionDto);
      fail(stringResponseEntity.toString());
    } catch (BadRequest400Exception ignored) { }
  }

  private void isNotFound(int id, MenuItemOptionDto optionDto) {
    try {
      final ResponseEntity<CreatedResponse> stringResponseEntity = menuItemApiController.addMenuItemOption(id, optionDto);
      fail(stringResponseEntity.toString());
    } catch (NotFound404Exception ignored) { }
  }

  // Test of deleteOption()
  
  @Test
  public void testDeleteOption() throws ResponseException {
    MenuItemDto menuItemDto = createPizzaMenuItem();
    ResponseEntity<CreatedResponse> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    CreatedResponse body = responseEntity.getBody();
    System.out.printf("Body: <%s>%n", body);
    Integer id = body.getId();
    
    MenuItem item = menuItemApiController.getMenuItemTestOnly(id);
    List<String> nameList = new LinkedList<>();
    for (MenuItemOption option : item.getAllowedOptions()) {
      nameList.add(option.getName());
    }
    assertThat(nameList, hasItems("pepperoni", "sausage", "mushrooms", "bell peppers", "olives", "onions"));


	  try {
		  ResponseEntity<Void> badResponseTwo = menuItemApiController.deleteOption(100000);
		  fail(badResponseTwo.toString());
    } catch (NotFound404Exception ignored) { }

    MenuItemOption removedOption = item.getAllowedOptions().iterator().next();
    String removedName = removedOption.getName();
    int removedId = removedOption.getId();
    assertTrue(hasName(item, removedName));
    assertNotNull(menuItemApiController.getMenuItemOptionTestOnly(removedId));
    ResponseEntity<Void> goodResponse = menuItemApiController.deleteOption(removedOption.getId());
    
    assertEquals(HttpStatus.OK, goodResponse.getStatusCode());
    
    List<MenuItemOption> allOptions = menuItemApiController.findAllOptionsTestOnly();
    for (MenuItemOption option: allOptions) {
      System.out.println(option);
    }
    
    item = menuItemApiController.getMenuItemTestOnly(id);
    assertFalse(hasName(item, removedName));
    assertNull(menuItemApiController.getMenuItemOptionTestOnly(removedId));
  }

  public static MenuItemDto createPizzaMenuItem() {
    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setName("Pizza");
    menuItemDto.setAllowedOptions(new LinkedList<>());
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("pepperoni", "0.30"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("sausage", "0.30"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("mushrooms", "0.15"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("bell peppers", "0.15"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("olives", "0.00"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("onions", "0.00"));
    menuItemDto.setItemPrice("5.95");
    return menuItemDto;
  }

  private static boolean hasName(MenuItem item, String optionName) {
    for (MenuItemOption option : item.getAllowedOptions()) {
      if (Objects.equals(option.getName(), optionName)) {
        return true;
      }
    }
    return false;
  }
}
