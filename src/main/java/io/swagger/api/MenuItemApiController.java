package io.swagger.api;

import java.util.List;
import javax.validation.Valid;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.disney.miguelmunoz.challenge.exception.BadRequest400Exception;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CreatedResponse;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;
import static com.disney.miguelmunoz.challenge.util.ResponseUtility.*;

// I removed the Generated annotation, because it turns inspections off for the whole class.
//@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@SuppressWarnings({"HardcodedFileSeparator", "SimplifiableAnnotation"})
@Controller
public class MenuItemApiController implements MenuItemApi {

  private static final Logger log = LoggerFactory.getLogger(MenuItemApiController.class);

  private final ObjectMapper objectMapper;

  private final MenuItemRepository menuItemRepository;
  
  private final MenuItemOptionRepository menuItemOptionRepository;

  @Autowired
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
      @PathVariable("menuItemId") final Integer menuItemId,
      @Valid @RequestBody final MenuItemOptionDto optionDto
  ) {
    return serveCreated(() -> {
      confirmNotEmpty(optionDto.getName()); // throws ResponseException
      MenuItemOption option = objectMapper.convertValue(optionDto, MenuItemOption.class);
      final MenuItem menuItem = menuItemRepository.findOne(menuItemId);
      confirmFound(menuItem, menuItemId);
      option.setMenuItem(menuItem);
      MenuItemOption savedOption = menuItemOptionRepository.save(option);
      return buildCreatedResponseWithId(savedOption.getId());
    });
  }

  @Override
  @RequestMapping(value = "/menuItem",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.PUT)
  public ResponseEntity<CreatedResponse> addMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
    return serveCreated(() -> {
      for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
        final String optionName = option.getName();
        if ((optionName == null) || optionName.isEmpty()) {
          throw new BadRequest400Exception("Missing Food Option name for item");
        }
      }
      MenuItem menuItem = convertMenuItem(menuItemDto);
      MenuItem savedItem = menuItemRepository.save(menuItem);
      return buildCreatedResponseWithId(savedItem.getId());
    });
  }

  private MenuItem convertMenuItem(@RequestBody @Valid final MenuItemDto menuItemDto) {
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
  public ResponseEntity<Void> deleteOption(@PathVariable("optionId") final Integer optionId) {
    return serveOK(() -> {
      log.debug("Deleting menuItemOption with id {}", optionId);

      MenuItemOption itemToDelete = menuItemOptionRepository.findOne(optionId);
      PojoUtility.confirmFound(itemToDelete, optionId);

      // Before I can successfully delete the menuItemOption, I first have to set its menuItem to null. If I don't
      // do that, the delete call will fail. It doesn't help to set Cascade to Remove in the @ManyToOne annotation in 
      // MenuItemOption. Since it's set to ALL in MenuItem's @OneToMany annotation, the Cascade value doesn't seem to 
      // affect this.
      itemToDelete.setMenuItem(null);
      menuItemOptionRepository.save(itemToDelete);

      menuItemOptionRepository.delete(itemToDelete);
      return null;
    });
  }

  @Override
  @RequestMapping(value = "/menuItem/{id}", produces = {"application/json"}, method = RequestMethod.GET)
  public ResponseEntity<MenuItemDto> getMenuItem(@PathVariable("id") final Integer id) {
    return serveOK(() -> {
      MenuItem menuItem = menuItemRepository.findOne(id);
      confirmFound(menuItem, id);
      return objectMapper.convertValue(menuItem, MenuItemDto.class);
    });
  }

  ////// Package-level methods for unit tests only! //////

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
