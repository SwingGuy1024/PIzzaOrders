/**
 * NOTE: This class is auto generated by the swagger code generator program (1.0.11).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.MenuItemDto;
import io.swagger.model.MenuItemOptionDto;
import io.swagger.annotations.*;
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

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T19:52:41.440Z")

@Api(value = "menuItem", description = "the menuItem API")
public interface MenuItemApi {

    @ApiOperation(value = "Add a MenuItem, with MenuItemOptions.", nickname = "addMenuItem", notes = "Add a MenuItem, complete with MenuItemOptions, to the database. Since MenuItemOptions are always linked to specific MenuItems, they are created, updated, and removed from the MenuItem API ", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Menu Item Created"),
        @ApiResponse(code = 400, message = "Bad Request") })
    @RequestMapping(value = "/menuItem",
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<String> addMenuItem(@ApiParam(value = "Complete menu item, with MenuItemOptions", required = true) @Valid @RequestBody MenuItemDto menuItem);


    @ApiOperation(value = "Add a menuItemOption", nickname = "addMenuItemOption", notes = "Add a MenuItemOption to a MenuItem ", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "MenuItemOption added"),
        @ApiResponse(code = 400, message = "Bad Request") })
    @RequestMapping(value = "/menuItem/addOption/{menuItemId}",
        method = RequestMethod.POST)
    ResponseEntity<String> addMenuItemOption(@ApiParam(value = "ID of the MenuItem getting the new option", required = true) @PathVariable("menuItemId") String menuItemId, @ApiParam(value = "MenuItemOption", required = true) @Valid @RequestBody MenuItemOptionDto option);


    @ApiOperation(value = "Delete a MenuItemOption", nickname = "deleteOption", notes = "Delete a MenuItemOption from a MenuItem.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "MenuItemOption added"),
        @ApiResponse(code = 400, message = "Bad Request") })
    @RequestMapping(value = "/menuItem/deleteOption/{optionId}",
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteOption(@ApiParam(value = "ID of the MenuItemOpton to delete", required = true) @PathVariable("optionId") String optionId);

}
