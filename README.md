# Swagger generated server

Spring Boot Server 


## Overview  

(The most important part of this demo is descirbed in the **Service Implementations** section, below.)

This server was generated by the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project.  
By using the [OpenAPI-Spec](https://github.com/swagger-api/swagger-core), you can easily generate a server stub.  
This is an example of building a swagger-enabled server in Java using the SpringBoot framework.  

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)  

Start your server as an simple java application.

This project requires Java 1.8 and Maven 3

To build:
 
`mvn clean install`

To run: 

`mvn spring-boot:run`

or

`java -jar target/miguelmunoz.challenge-0.0.1-SNAPSHOT.jar`

## REST API Documentation

You can view the api documentation in swagger-ui by launching the server, then go to 
`http://localhost:8080/NeptuneDreams/CustomerOrders/1.0.0/swagger-ui.html`

Change default port value in application.properties

## Database
You do not need to launch a database server to run this application. I use an embedded h2 database. This is great for demo purposes, because you don't need to launch a server, and it's a java database, so it runs with very little configuration.

## Service Implementations

To ensure consistency in how the services are written, and to reduce the amount of boilerplate code, all the services use a variant of the `ResponseUtility.serve()` method. This allows the service to focus solely on the task of generating the service data, and not worry about creating the ResponseEntity or generating an error response. In case of an error, the service need only throw a ResponseException, which includes an HttpStatus value. By convention, this exception is thrown by methods beginning with the word "confirm." For example, if a service requests an Entity with a specific ID, and the item may return null, the service should call `PojoUtility.confirmNotNull(entity, id);` If the value is `null`, the `confirmNotNull()` method will throw a ResponseException with a NOT_FOUND status, and include the id in the error message.

So a service method that needs to return an instance of `MenuItemDto` would look something like this:

``` java class X {
1  @RequestMapping(
   value = "/menuItem/{id}", 
   produces = {"application/json"}, 
   method = RequestMethod.GET)
2  public ResponseEntity<MenuItemDto> getMenuItem(@PathVariable("id") final Integer id) {
3    return serve(HttpStatus.OK, () -> {
4      MenuItem menuItem = menuItemRepository.findOne(id); // Get from the database
5      confirmNotNull(menuItem, id);           // throws ResponseException
6      return objectMapper.convertValue(menuItem, MenuItemDto.class);
7    });
8  }
```


So, on line 3, we specify an OK status if the method returns successfully. We also begin the lambda expression that does the work of this service.

On line 5, we test for null, using the `confirmNotNull()` method. If `menuItem` is null, it will throw `ResponseException` with an `HttpStatus` of `NOT_FOUND`. We don't need to catch it, because the `serve()` method, on line 3, does that.

The call to the `serve()` method takes care four boilerplate details:
1. It adds the return value (an instance of MenuItemDto) to the `ResponseEntity` on successful completion.
1. It sets the specified HttpStatus, which in this example is `HttpStatus.OK`.
1. It generates the proper error response, with an error status code of NOT_FOUND, if the `confirmNotNull()` method throws a `ResponseException`.
1. It logs the error message and exception.

The lambda expression creates an object of type ServiceMethod. This is a simple funcitonal interface:

```java
@FunctionalInterface
public interface ServiceMethod<T> {
  T doService() throws ResponseException;
}
```

The `serve()` method has this signature: 

`  public static <T> ResponseEntity<T> serve(HttpStatus successStatus, ServiceMethod<T> method)`

The only boilerplate code in the example is the `@RequestMapping` annotation and the method signature, both of which are generated by Swagger.

### Sample `confirmXxx()` methods. 
All of these may throw a `ResponseException`. I've adopted the convention that all methods that may throw `ResponseException` start with the word *confirm.*

* `confirmNotNull(T entity, Object id) throws ResponseException` Confirms the returned entity with specified id is not null. (The `id` parameter is used to generate a more useful error message.)
* `confirmNeverNull(T object) throws ResponseException` Used for values that are not entities.
* `confirmNull(Object object) throws ResponseException` This is useful to ensure a resource doesn't already exist.
* `confirmEqual(T expected, T actual) throws ResponseException`
* `confirmAndDecodeInteger(final String id) throws ResponseException` This Parses the String into an Integer. A better name might be just `decodeInteger()`, but it starts with `confirm` to keep with the convention that all methods that throw a `ResponseException` start with `confirm`.

People have asked why I didn't use the word *validate,* since it's pretty standard. I decided not to use it to be clear that these methods are not a part of any particular validation framework.


## Data Model 
### Assumptions

A Menu item consists of options. Each menu item has a price, as does each option. (Option prices may be zero.) An order consists of a menu item and a list of options.

An order may calculate a price based on the Menu Item's base cost and the options chosen. 

When an order is opened, the time is recorded. (I have no idea if that's useful, but it may help in searching.) At this point, the order may be either canceled or completed. If it's canceled, it's removed from the database. If it's completed, it is marked complete and kept in the database.

Orders may be searched by ID 

I'm not sure if my API is most useful for a UI developer. I prefer to ask the UI developers what they need, then build the API around their needs. That said, I have APIs to define menu items, and add options to them. I have APIs to create an order, to add options to either an order or a MenuItem, and to search for completed or open orders in a given date range.

### JPA Entities

#### 1. MenuItem
A MenuItem consists of a name, price, and list of MenuItemOptions (below). The list consists of all possible options for this menu item. MenuItem has a One-to-Many relationship with MenuItemOption. It also includes a price.

#### 2. MenuItemOption
A MenuItemOption adds an option to aMenuItem (below). It also has a delta price, which is the amount the price changes if the guest chooses this option.

#### 3. CustomerOrder
A Food order is an actual order. It has a final price, a boolean to record when it has been completed and delivered, and an order date and completion date, and a list of MenuItemOptions. Unlike the MenuItem, the list of options is all the chosen options, rather than the available options. Also, unlike MenuItem, the CustomerOrder has a Many-To-Many relationship with MenuItemOption.

## Testing
The testing application properties specify an in-memory database, so changes get wiped out from test to test. This greatly facilitates testing.

The Controller classes have public method which are called by the server, and package-level methods that are only for testing. All of these package methods are named `xxxXxxxTestOnly` to discourage their use even if somebody puts a class to the right package. 

