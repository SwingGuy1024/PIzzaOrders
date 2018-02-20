package io.swagger.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import com.disney.miguelmunoz.challenge.entities.FoodOption;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.FoodOptionDto;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static com.disney.miguelmunoz.challenge.util.ResponseUtility.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Controller
public class MenuItemApiController implements MenuItemApi {

  private static final Logger log = LoggerFactory.getLogger(MenuItemApiController.class);

  @Autowired
  private final ObjectMapper objectMapper;

  @Autowired
  private final MenuItemRepository menuItemRepository;
  
//  @Autowired
//  private final MenuItemOptionRepository menuItemOptionRepository;
  
  public MenuItemApiController(
      ObjectMapper objectMapper, 
//      MenuItemOptionRepository menuItemOptionRepository,
      MenuItemRepository menuItemRepository
  ) {
    this.objectMapper = objectMapper;
    this.menuItemRepository = menuItemRepository;
//    this.menuItemOptionRepository = menuItemOptionRepository;
  }

  @Override
  public ResponseEntity<String> addMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
    try {
      for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
        final FoodOptionDto foodOption = option.getFoodOption();
        if (foodOption == null || foodOption.getId() == null) {
          throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing Food Option for item");
        }
      }
      MenuItem menuItem = convertMenuItem(menuItemDto);
//      MenuItem menuItem = objectMapper.convertValue(menuItemDto, MenuItem.class);
      MenuItem savedItem = menuItemRepository.save(menuItem);
      return makeCreatedResponseWithId(savedItem.getId());
    } catch (ResponseException e) {
      return makeStringResponse(e);
    }
  }

  private MenuItem convertMenuItem(final @Valid @RequestBody MenuItemDto menuItemDto) {
    MenuItemOption[] menuItemOptions = getMenuItemOptions(menuItemDto);
    MenuItem menuItem = new MenuItem();
    for (MenuItemOption option: menuItemOptions) {
      option.setMenuItem(menuItem);
    }
    menuItem.setMenuItemOptionList(Arrays.asList(menuItemOptions));
    menuItem.setItemPrice(new BigDecimal(menuItemDto.getItemPrice()));
    return menuItem;
  }

  private MenuItemOption[] getMenuItemOptions(final MenuItemDto menuItemDto) {
    final List<MenuItemOptionDto> allowedOptions = menuItemDto.getAllowedOptions();
    MenuItemOption[] array = new MenuItemOption[allowedOptions.size()];

    int index = 0;
    for (MenuItemOptionDto menuItemOptionDto : allowedOptions) {
      final MenuItemOption menuItemOption = new MenuItemOption();
      menuItemOption.setFoodOption(objectMapper.convertValue(menuItemOptionDto.getFoodOption(), FoodOption.class));
      menuItemOption.setDeltaPrice(new BigDecimal(menuItemOptionDto.getDeltaPrice()));
      array[index++] = menuItemOption;
    }
    return array;
  }

  ////// For unit tests //////
  
  MenuItem getMenuItem(int id) {
    return menuItemRepository.getOne(id);
  }
}
