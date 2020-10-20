package com.disney.miguelmunoz.challenge.server;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.disney.miguelmunoz.challenge.entities.MenuItem;
import com.disney.miguelmunoz.challenge.entities.MenuItemOption;
import com.disney.miguelmunoz.framework.PojoUtility;
import com.disney.miguelmunoz.framework.exception.BadRequest400Exception;
import com.disney.miguelmunoz.challenge.repositories.MenuItemOptionRepository;
import com.disney.miguelmunoz.challenge.repositories.MenuItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.api.MenuItemApi;
import io.swagger.model.CreatedResponse;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import static com.disney.miguelmunoz.framework.PojoUtility.*;
import static com.disney.miguelmunoz.framework.ResponseUtility.*;

//@SuppressWarnings({"HardcodedFileSeparator", "SimplifiableAnnotation"})
@Controller
public class MenuItemApiController implements MenuItemApi {

  private static final Logger log = LoggerFactory.getLogger(MenuItemApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  private final MenuItemRepository menuItemRepository;

  private final MenuItemOptionRepository menuItemOptionRepository;

  @Autowired
  public MenuItemApiController(
      ObjectMapper objectMapper,
      HttpServletRequest request,
      MenuItemOptionRepository menuItemOptionRepository,
      MenuItemRepository menuItemRepository
  ) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.menuItemRepository = menuItemRepository;
    this.menuItemOptionRepository = menuItemOptionRepository;
    log.info("New MenuItemApiController()");
  }

  @Override
  public Optional<ObjectMapper> getObjectMapper() {
    return Optional.ofNullable(objectMapper);
  }

  @Override
  public Optional<HttpServletRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<CreatedResponse> addMenuItemOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
    logHeaders();
    return serveCreatedById(() -> {
      confirmNotEmpty(optionDto.getName()); // throws ResponseException
      MenuItemOption menuItemOption = objectMapper.convertValue(optionDto, MenuItemOption.class);
      final MenuItem menuItem = menuItemRepository.findOne(menuItemId);
      confirmEntityFound(menuItem, menuItemId);
      menuItemOption.setMenuItem(menuItem);
      MenuItemOption savedOption = menuItemOptionRepository.save(menuItemOption);
      return savedOption.getId();
    });
  }

  @Override
  public ResponseEntity<CreatedResponse> addMenuItem(final MenuItemDto menuItemDto) {
    logHeaders();
    return serveCreatedById(() -> {
      for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
        final String optionName = option.getName();
        if ((optionName == null) || optionName.isEmpty()) {
          throw new BadRequest400Exception("Missing Food Option name for item");
        }
      }
      MenuItem menuItem = convertMenuItem(menuItemDto);
      MenuItem savedItem = menuItemRepository.save(menuItem);
      return savedItem.getId();
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
  public ResponseEntity<Void> deleteOption(final Integer optionId) {
    logHeaders();
    return serveOK(() -> {
      log.debug("Deleting menuItemOption with id {}", optionId);

      MenuItemOption itemToDelete = menuItemOptionRepository.findOne(optionId);
      PojoUtility.confirmEntityFound(itemToDelete, optionId);

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
  public ResponseEntity<MenuItemDto> getMenuItem(final Integer id) {
    logHeaders();
    return serveOK(() -> {
      MenuItem menuItem = menuItemRepository.findOne(id);
      confirmEntityFound(menuItem, id);
      return objectMapper.convertValue(menuItem, MenuItemDto.class);
    });
  }

  @Override
  public ResponseEntity<List<MenuItemDto>> getAll() {
    logHeaders();
    return serveOK(this::getAllMenuItems);
  }

  void logHeaders() {
//    getRequest().ifPresent((r) -> {
//      Enumeration<String> headerNames = r.getHeaderNames();
//      log.info("{} headers", countTokens(headerNames));
//      headerNames = r.getHeaderNames();
//      while (headerNames.hasMoreElements()) {
//        String hName = headerNames.nextElement();
//        log.info("{}: {}", hName, r.getHeader(hName));
//      }
//    });
//  }
//
//  private int countTokens(Enumeration<?> enumeration) {
//    int count = 0;
//    while (enumeration.hasMoreElements()) {
//      enumeration.nextElement();
//      count++;
//    }
//    return count;
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

  private List<MenuItemDto> getAllMenuItems() {
    return menuItemRepository
        .findAll()
        .stream()
        .map((m) -> objectMapper.convertValue(m, MenuItemDto.class))
        .collect(Collectors.toList());
  }
}
