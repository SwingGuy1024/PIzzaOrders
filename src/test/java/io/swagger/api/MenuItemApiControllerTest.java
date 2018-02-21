package io.swagger.api;

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
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 1:44 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"CallToNumericToString", "MagicCharacter"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Component
public class MenuItemApiControllerTest {
  
  @Autowired
  private MenuItemApiController menuItemApiController;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  // Tests of addMenuItem()
  
  @Test
  public void testAddMenuItemBadInput() {
    MenuItemOptionDto menuItemOption = new MenuItemOptionDto();
    menuItemOption.setName("");
    menuItemOption.setDeltaPrice("5.00");

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Collections.singletonList(menuItemOption));
    menuItemDto.setName("BadItem");
    menuItemDto.setItemPrice("0.50");
    ResponseEntity<CreatedResponse> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
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
    Integer id = decodeIdString(body.getId());
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
    MenuItemOptionDto oliveOption = new MenuItemOptionDto();
    oliveOption.setName(name);
    oliveOption.setDeltaPrice(price);
    return oliveOption;
  }

  // Tests of addMenuItemOption()
  
  @Test
  public void testAddOptionBadInput() {
    isBadRequest(menuItemApiController.addMenuItemOption("5", makeMenuItemOptionDto("olives","X")));
    isBadRequest(menuItemApiController.addMenuItemOption("5", makeMenuItemOptionDto("olives","0.x0")));
    isBadRequest(menuItemApiController.addMenuItemOption("5", makeMenuItemOptionDto("olives","1,000.00")));
    isBadRequest(menuItemApiController.addMenuItemOption("5", makeMenuItemOptionDto("","0.40")));
    isBadRequest(menuItemApiController.addMenuItemOption("x", makeMenuItemOptionDto("olives","0.30")));
    isBadRequest(menuItemApiController.addMenuItemOption("x", makeMenuItemOptionDto(null,"0.30")));
  }

  private void isBadRequest(final ResponseEntity<CreatedResponse> stringResponseEntity) {
    if (!Objects.equals(stringResponseEntity.getStatusCode(), HttpStatus.BAD_REQUEST)) {
      //noinspection ProhibitedExceptionThrown
      throw new RuntimeException(String.format(
          "status %s with String \"%s\" should be BAD_REQUEST", 
          stringResponseEntity.getStatusCode(), 
          stringResponseEntity.getBody()
      ));
    }
  }
  
  // Test of deleteOption()
  
  @Test
  public void testDeleteOption() throws ResponseException {
    MenuItemDto menuItemDto = createPizzaMenuItem();
    ResponseEntity<CreatedResponse> responseEntity = menuItemApiController.addMenuItem(menuItemDto);
    CreatedResponse body = responseEntity.getBody();
    System.out.printf("Body: <%s>%n", body);
    Integer id = decodeIdString(body.getId());
    
    MenuItem item = menuItemApiController.getMenuItemTestOnly(id);
    Hibernate.initialize(item.getAllowedOptions());
    List<String> nameList = new LinkedList<>();
    for (MenuItemOption option : item.getAllowedOptions()) {
      nameList.add(option.getName());
    }
    assertThat(nameList, hasItems("pepperoni", "sausage", "mushrooms", "bell peppers", "olives", "onions"));
    
    
    ResponseEntity<Void> badResponseOne = menuItemApiController.deleteOption("BAD");
    assertEquals(HttpStatus.BAD_REQUEST, badResponseOne.getStatusCode());

    ResponseEntity<Void> badResponseTwo = menuItemApiController.deleteOption("100000");
    assertEquals(HttpStatus.BAD_REQUEST, badResponseTwo.getStatusCode());

    MenuItemOption removedOption = item.getAllowedOptions().iterator().next();
    String removedName = removedOption.getName();
    int removedId = removedOption.getId();
    assertTrue(hasName(item, removedName));
    assertNotNull(menuItemApiController.getMenuItemOptionTestOnly(removedId));
    ResponseEntity<Void> goodResponse = menuItemApiController.deleteOption(removedOption.getId().toString());
    
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

  private static MenuItemOption makeOption(String name, String price, MenuItem menuItem) {
    MenuItemOption option = new MenuItemOption();
    option.setName(name);
    option.setDeltaPrice(new BigDecimal(price));
    option.setMenuItem(menuItem);
    return option;
  }
}
