package io.swagger.api;

import java.util.List;
import javax.validation.Valid;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.disney.miguelmunoz.challenge.util.ResponseUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
  @RequestMapping(value = "/menuItem/addOption/{menuItemId}",
      produces = {"application/json"},
      method = RequestMethod.POST)
  public ResponseEntity<CreatedResponse> addMenuItemOption(
      @PathVariable("menuItemId") String menuItemId, 
      @Valid @RequestBody MenuItemOptionDto optionDto
  ) {
    try {
      confirmNotEmpty(optionDto.getName()); // throws ResponseException
      MenuItemOption option = objectMapper.convertValue(optionDto, MenuItemOption.class);
      Integer itemId = decodeIdString(menuItemId); // throws ResponseException
      final MenuItem menuItem = menuItemRepository.findOne(itemId);
      confirmNotNull(menuItem, itemId);
      option.setMenuItem(menuItem);
      MenuItemOption savedOpton = menuItemOptionRepository.save(option);
      return makeCreatedResponseWithId(savedOpton.getId().toString());
    } catch (Exception | Error e) {
      return makeErrorResponse(e);
    }
  }

  @Override
  @RequestMapping(value = "/menuItem",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.PUT)
  public ResponseEntity<CreatedResponse> addMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
    try {
      for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
        final String optionName = option.getName();
        if (optionName == null || optionName.isEmpty()) {
          throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing Food Option name for item");
        }
      }
      MenuItem menuItem = convertMenuItem(menuItemDto);
      MenuItem savedItem = menuItemRepository.save(menuItem);
      return makeCreatedResponseWithId(savedItem.getId().toString());
    } catch (Exception | Error e) {
      return makeErrorResponse(e);
    }
  }

  private MenuItem convertMenuItem(final @Valid @RequestBody MenuItemDto menuItemDto) {
    final MenuItem menuItem = objectMapper.convertValue(menuItemDto, MenuItem.class);

    // objectMapper doesn't set the menuItems in the options, because it can't handle circular references, so we
    // set them here.
    for (MenuItemOption option : menuItem.getAllowedOptions()) {
      option.setMenuItem(menuItem);
    }

    return menuItem;
  }

  @Override
  @RequestMapping(value = "/menuItem/deleteOption/{optionId}",
      produces = {"application/json"},
      method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteOption(@PathVariable("optionId") String optionId) {
    try {
      Integer id = decodeIdString(optionId);
      log.debug("Deleting menuItemOption with id {} evaluates to {}", optionId, id);

      MenuItemOption itemToDelete = menuItemOptionRepository.findOne(id);

      // Before I can successfully delete the menuItemOption, I first have to set its menuItem to null. If I don't
      // do that, the delete call will fail. It doesn't help to set Cascade to Remove in the @ManyToOne annotation in 
      // MenuItemOption. Since it's set to ALL in MenuItem's @OneToMany annotation, the Cascade value doesn't seem to 
      // affect this.
      itemToDelete.setMenuItem(null);
      menuItemOptionRepository.save(itemToDelete);

      menuItemOptionRepository.delete(itemToDelete);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception | Error e) {
      return ResponseUtility.makeGenericErrorResponse(e);
    }
  }

  @Override
  @RequestMapping(value = "/menuItem/{id}", produces = {"application/json"}, method = RequestMethod.GET)
  public ResponseEntity<MenuItemDto> getMenuItem(
      @PathVariable("id") 
      final Integer id) {
    MenuItemDto dto = null;
    try {
      MenuItem menuItem = menuItemRepository.findOne(id);
      confirmNotNull(menuItem, id);
      dto = objectMapper.convertValue(menuItem, MenuItemDto.class);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    } catch (ResponseException e) {
      return ResponseUtility.makeGenericErrorResponse(e);
    }
  }

  ////// Package-leve methods for unit tests only! //////

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
