package com.disney.miguelmunoz.challenge.util;

import com.disney.miguelmunoz.challenge.exception.ResponseException;
import io.swagger.model.CreatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 11:46 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum ResponseUtility {
  ;
  private static final Logger log = LoggerFactory.getLogger(ResponseUtility.class);

  public static InternalServerError500Exception logAndMakeGenericErrorResponse(RuntimeException t) {
    log.error(t.getMessage(), t);
    return new InternalServerError500Exception(t);
  }
  

  private static CreatedResponse buildCreatedResponseWithId(Integer id) {
    CreatedResponse response = new CreatedResponse();
    response.setId(id);
    return response;
  }

  /**
   * Serve the data, using HttpStatus.CREATED as the response if successful. This method delegates the work to serve().
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @param <T> The return type
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, ServiceMethod) 
   * @see ServiceMethod#doService() 
   */
  public static <T> ResponseEntity<T> serveCreated(ServiceMethod<T> method) throws ResponseException {
    assert method != null;
    return serve(HttpStatus.CREATED, method);
  }

  /**
   * Serve the creation request, using HttpStatus.CREATED as the response if successful. This method delegates the work to serve().
   *
   * @param method A method that creates the resource and returns the id of the created resource as an Integer.
   * @return A ResponseEntity that holds a CreatedResponse, which itself holds the id of the created resource.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, ServiceMethod)
   * @see ServiceMethod#doService()
   */
  public static ResponseEntity<CreatedResponse> serveCreatedById(ServiceMethod<Integer> method) throws ResponseException {
    assert method != null;
    return serveCreated(() -> buildCreatedResponseWithId(method.doService()));
  }

  /**
   * Serve the creation request, using the specified status as the response if successful. This method delegates the work to serve().
   *
   * @param successStatus The status to use if the ServiceMethod's doService() method (the lambda expression) completes successfully.
   * @param method A method that creates the resource and returns the id of the created resource as an Integer.
   * @return A ResponseEntity that holds a CreatedResponse, which itself holds the id of the created resource.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, ServiceMethod)
   * @see ServiceMethod#doService()
   */
  public static ResponseEntity<CreatedResponse> serveById(HttpStatus successStatus, ServiceMethod<Integer> method) throws ResponseException {
    assert method != null;
    return serve(successStatus, () -> buildCreatedResponseWithId(method.doService()));
  }

  /**
   * Serve the data, using HttpStatus.OK as the response if successful. This method delegates the work to serve().
   *
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @param <T>    The return type
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, ServiceMethod)
   * @see ServiceMethod#doService()
   */
  public static <T> ResponseEntity<T> serveOK(ServiceMethod<T> method) throws ResponseException {
    return serve(HttpStatus.OK, method);
  }

  /**
   * <p>Serve the data, specifying the HttpStatus to be used if successful, and a ServiceMethod to execute, which 
   * will usually be written as a lambda expression by the calling method. This will call the ServiceMethod's
   * doService() method inside a try/catch block. If doService() completes successfully, this method will return
   * the result packed inside a ResponseEntity object, using the specified successStatus. If doService throws an 
   * Exception, this method will return a ResponseEntity with the proper error HttpStatus and error message.
   * </p><p>
   * Since the doService() method is declared to return a ResponseException, the provided lambda expression need only
   * throw a ResponseException on failure. The error handling portion of this method will use the HttpStatus specified
   * in the ResponseException to generate the ResponseEntity.
   * </p><p>
   * This allows the developer to implement functional part of the service method inside a lambda expression without 
   * bothering with the boilerplate code used to package the successful response or handle any error.
   * </p><p>
   * <strong>Example:</strong><br>
   * </p>
   * <pre>
   *   {@literal @Override}
   *   {@literal @RequestMapping}(value = "/menuItem/{id}", produces = {"application/json"}, method = RequestMethod.GET)
   *   public ResponseEntity{@literal <MenuItemDto>} getMenuItem(@PathVariable("id") final Integer id) {
   *     return serve(HttpStatus.OK, () -> {
   *       MenuItem menuItem = menuItemRepository.findOne(id);
   *       confirmFound(menuItem, id); // throws NotFound404Exception if null
   *       MenuItemDto dto = objectMapper.convertValue(menuItem, MenuItemDto.class);
   *       return dto;
   *     });
   *   }
   * </pre>
   * @param successStatus The status to use if the ServiceMethod's doService() method (the lambda expression) completes
   *                      successfully.
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @param <T> The return type. 
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @throws ResponseException if the method fails
   * @see ServiceMethod#doService() 
   * @see ResponseException
   */
  public static <T> ResponseEntity<T> serve(HttpStatus successStatus, ServiceMethod<T> method) throws ResponseException {
    assert method != null;
    try {
      // All of the work of the service goes into the method.doService() implementation.
      return new ResponseEntity<>(method.doService(), successStatus);
    } catch (ResponseException e) {
      log.debug(e.getLocalizedMessage(), e);
      throw e;
    } catch (RuntimeException re) {
//      //noinspection unchecked
//      return (ResponseEntity<T>) 
      throw logAndMakeGenericErrorResponse(re); // Sets HttpStatus to INTERNAL_SERVER_ERROR
    } catch (Error e) {

      // Errors are logged as errors, since the're caused by serious issues like bugs or resource depletion.
      log.error(e.getMessage(), e);
      //noinspection ProhibitedExceptionThrown
      throw e;
    }
  }
}
