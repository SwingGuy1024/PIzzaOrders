/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.15).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.CreatedResponse;
import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-09-14T10:11:39.090Z")

@Api(value = "menuItem", description = "the menuItem API")
@RequestMapping(value = "/NeptuneDreams/CustomerOrders/1.0.0")
public interface MenuItemApi {

    Logger log = LoggerFactory.getLogger(MenuItemApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Add a MenuItem, with optional MenuItemOptions.", nickname = "addMenuItem", notes = "Add a MenuItem, complete with MenuItemOptions, to the database. Since MenuItemOptions are always linked to specific MenuItems, they are created, updated, and removed by using the MenuItem API. More options may be added later with the /MenuItem/addOption/ API. ", response = CreatedResponse.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Menu Item Created", response = CreatedResponse.class),
        @ApiResponse(code = 400, message = "Bad Request", response = CreatedResponse.class) })
    @RequestMapping(value = "/menuItem",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<CreatedResponse> addMenuItem(@ApiParam(value = "Complete menu item, with MenuItemOptions" ,required=true )  @Valid @RequestBody MenuItemDto menuItem) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"id\" : 0,  \"message\" : \"message\",  \"body\" : \"body\"}", CreatedResponse.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default MenuItemApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Add a menuItemOption", nickname = "addMenuItemOption", notes = "Add a MenuItemOption to a MenuItem. ", response = CreatedResponse.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "MenuItemOption added", response = CreatedResponse.class),
        @ApiResponse(code = 400, message = "Bad Request", response = CreatedResponse.class) })
    @RequestMapping(value = "/menuItem/addOption/{menuItemId}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<CreatedResponse> addMenuItemOption(@ApiParam(value = "ID of the MenuItem getting the new option",required=true) @PathVariable("menuItemId") Integer menuItemId,@ApiParam(value = "MenuItemOption" ,required=true )  @Valid @RequestBody MenuItemOptionDto option) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"id\" : 0,  \"message\" : \"message\",  \"body\" : \"body\"}", CreatedResponse.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default MenuItemApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Delete a MenuItemOption", nickname = "deleteOption", notes = "Delete a MenuItemOption from a MenuItem.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "MenuItemOption deleted"),
        @ApiResponse(code = 400, message = "Bad Request", response = CreatedResponse.class) })
    @RequestMapping(value = "/menuItem/deleteOption/{optionId}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteOption(@ApiParam(value = "ID of the MenuItemOpton to delete",required=true) @PathVariable("optionId") Integer optionId) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default MenuItemApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Get all menu items.", nickname = "getAll", notes = "Retrieve all menu items ", response = MenuItemDto.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Found", response = MenuItemDto.class, responseContainer = "List") })
    @RequestMapping(value = "/menuItem",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<MenuItemDto>> getAll() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {  \"allowedOptions\" : [ {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  }, {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  } ],  \"name\" : \"name\",  \"itemPrice\" : \"itemPrice\",  \"id\" : 1}, {  \"allowedOptions\" : [ {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  }, {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  } ],  \"name\" : \"name\",  \"itemPrice\" : \"itemPrice\",  \"id\" : 1} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default MenuItemApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Gets a menuItem by ID", nickname = "getMenuItem", notes = "Gets a MenuItem by its ID.", response = MenuItemDto.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Found", response = MenuItemDto.class),
        @ApiResponse(code = 404, message = "NotFound") })
    @RequestMapping(value = "/menuItem/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<MenuItemDto> getMenuItem(@ApiParam(value = "id of menuItem to find",required=true) @PathVariable("id") Integer id) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"allowedOptions\" : [ {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  }, {    \"deltaPrice\" : 0.80082819046101150206595775671303272247314453125,    \"name\" : \"name\",    \"id\" : 6  } ],  \"name\" : \"name\",  \"itemPrice\" : \"itemPrice\",  \"id\" : 1}", MenuItemDto.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default MenuItemApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
