package io.swagger.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.disney.miguelmunoz.challenge.util.ResponseUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  
  @Autowired
  private final MenuItemOptionRepository menuItemOptionRepository;
  
  public MenuItemApiController(
      ObjectMapper objectMapper, 
      MenuItemOptionRepository menuItemOptionRepository,
      MenuItemRepository menuItemRepository
  ) {
    this.objectMapper = objectMapper;
    this.menuItemRepository = menuItemRepository;
    this.menuItemOptionRepository = menuItemOptionRepository;
  }

  @Override
  public ResponseEntity<String> addMenuItemOption(final String menuItemId, final MenuItemOptionDto optionDto) {
    try {
      notEmpty(optionDto.getName()); // throws ResponseException
      MenuItemOption option = objectMapper.convertValue(optionDto, MenuItemOption.class);
      Integer itemId = decodeIdString(menuItemId); // throws ResponseException
      final MenuItem menuItem = menuItemRepository.findOne(itemId);
      option.setMenuItem(menuItem);
      MenuItemOption savedOpton = menuItemOptionRepository.save(option);
      return makeCreatedResponseWithId(savedOpton.getId());
    } catch (Exception | Error e) {
      return makeStringResponse(e);
    }
  }

  @Override
  public ResponseEntity<String> addMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
    try {
      for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
        final String optionName = option.getName();
        if (optionName == null || optionName.isEmpty()) {
          throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing Food Option name for item");
        }
      }
      MenuItem menuItem = convertMenuItem(menuItemDto);
      MenuItem savedItem = menuItemRepository.save(menuItem);
      return makeCreatedResponseWithId(savedItem.getId());
    } catch (Exception | Error e) {
      return makeStringResponse(e);
    }
  }

  private MenuItem convertMenuItem(final @Valid @RequestBody MenuItemDto menuItemDto) {
    final MenuItem menuItem = objectMapper.convertValue(menuItemDto, MenuItem.class);

    // convertValue() gets everything but the allowedValues.
    List<MenuItemOptionDto> optionDtos = menuItemDto.getAllowedOptions();
    List<MenuItemOption> options = objectMapper.convertValue(optionDtos, new TypeReference<List<MenuItemOption>>() { });
//    List<MenuItemOption> options = convertList(optionDtos);
    for (MenuItemOption option : options) {
      option.setMenuItem(menuItem);
    }
    menuItem.setAllowedOptionsList(options);

    return menuItem;
  }

  @Override
  public ResponseEntity<Void> deleteOption(final String optionId) {
    try {
      Integer id = decodeIdString(optionId);
      log.debug("Deleting menuItemOption with id {} evaluates to {}", optionId, id);

      // Before I can successfully delete the menuItemOption, I first have to set its menuItem to null. If I don't
      // do that, the delete call will fail. I don't know why.
      MenuItemOption itemToDelete = menuItemOptionRepository.findOne(id);
      itemToDelete.setMenuItem(null);
      menuItemOptionRepository.save(itemToDelete);
      menuItemOptionRepository.delete(itemToDelete);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception | Error e) {
      return ResponseUtility.makeResponse(e);
    }
  }

  ////// For unit tests only! //////
  
  MenuItem getMenuItemTestOnly(int id) {
    return menuItemRepository.findOne(id);
  }
  
  MenuItemOption getMenuItemOptionTestOnly(int id) {
    return menuItemOptionRepository.findOne(id);
  }
  
  List<MenuItemOption> findAllOptionsTestOnly() {
    return menuItemOptionRepository.findAll();
  }
}
