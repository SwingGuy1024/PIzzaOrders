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
@SuppressWarnings("HardCodedStringLiteral")
public enum ResponseUtility {
  ;
  private static final Logger log = LoggerFactory.getLogger(ResponseUtility.class);

  public static ResponseEntity<CreatedResponse> logAndMakeGenericErrorResponse(RuntimeException t) {
    log.debug(t.getMessage(), t);
    return new ResponseEntity<>(makeError(t), HttpStatus.BAD_REQUEST);
  }
  
  public static ResponseEntity<?> logAndMakeErrorResponse(ResponseException ex) {
    final HttpStatus httpStatus = ex.getHttpStatus();
    log.debug(httpStatus.toString(), ex);
    return new ResponseEntity<>(makeErrorFromResponseException(ex), httpStatus);
  }

  private static CreatedResponse makeErrorFromResponseException(ResponseException ex) {
    HttpStatus status = ex.getHttpStatus();
    CreatedResponse error = new CreatedResponse();
    error.setHttpStatus(status.value());
    error.setMessage(String.format("%s: %s", status.getReasonPhrase(), ex.getMessage()));
    log.debug(String.format("%s: %s", status, error.getMessage()), ex);
    return error;
  }

  private static CreatedResponse makeError(RuntimeException t) {
    CreatedResponse error = new CreatedResponse();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    error.setHttpStatus(status.value());
    error.setMessage(t.getMessage());
    log.debug(String.format("%s: %s", status, error.getMessage()), t);
    return error;
  }

  public static CreatedResponse buildCreatedResponseWithId(String id) {
    CreatedResponse response = new CreatedResponse();
    response.setId(id);
    return response;
  }

  /**
   * Serve the data, using HttpStatus.CREATED as the response if successful. This method delegates the work to serve().
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @param <T> The return type
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @see #serve(HttpStatus, ServiceMethod) 
   * @see ServiceMethod#doService() 
   */
  public static <T> ResponseEntity<T> serveCreated(ServiceMethod<T> method) {
    assert method != null;
    return serve(HttpStatus.CREATED, method);
  }

  /**
   * Serve the data, using HttpStatus.OK as the response if successful. This method delegates the work to serve().
   *
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @param <T>    The return type
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @see #serve(HttpStatus, ServiceMethod)
   * @see ServiceMethod#doService()
   */
  public static <T> ResponseEntity<T> serveOK(ServiceMethod<T> method) {
    return serve(HttpStatus.OK, method);
  }

  /**
   * Serve the data, specifying the HttpStatus to be used if successful, and a ServiceMethod to execute, which 
   * will usually be written as a lambda expression by the calling method. This will call the ServiceMethod's
   * doService() method inside a try/catch block. If doService() completes successfully, this method will return
   * the result packed inside a ResponseEntity object, using the specified successStatus. If doService throws an 
   * Exception, this method will return a ResponseEntity with the proper error HttpStatus and error message.
   * <p/>
   * Since the doService() method is declared to return a ResponseException, the provided lambda expression need only
   * throw a ResponseException on failure. The error handling portion of this method will use the HttpStatus specified
   * in the ResponseException to generate the ResponseEntity.
   * <p/>
   * This allows the developer to implement functional part of the service method inside a lambda expression without 
   * bothering with the boilerplate code used to package the successful response or handle any error.
   * <p/>
   * <strong>Example:</strong><br>
   * <pre>
   *   {@literal @Override}
   *   {@literal @RequestMapping}(value = "/menuItem/{id}", produces = {"application/json"}, method = RequestMethod.GET)
   *   public ResponseEntity{@literal <MenuItemDto>} getMenuItem(@PathVariable("id") final Integer id) {
   *     return serve(HttpStatus.OK, () -> {
   *       MenuItem menuItem = menuItemRepository.findOne(id);
   *       confirmNotNull(menuItem, id); // throws ResponseException if null
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
   * @see ServiceMethod#doService() 
   * @see ResponseException
   */
  public static <T> ResponseEntity<T> serve(HttpStatus successStatus, ServiceMethod<T> method) {
    assert method != null;
    try {
      // All of the work of the service goes into the method.doService() implementation.
      return new ResponseEntity<>(method.doService(), successStatus);
    } catch (ResponseException e) {
      //noinspection unchecked
      return (ResponseEntity<T>) logAndMakeErrorResponse(e); // Gets the HttpStatus from the ResponseException
    } catch (RuntimeException re) {
      //noinspection unchecked
      return (ResponseEntity<T>) logAndMakeGenericErrorResponse(re); // Sets HttpStatus to BAD_REQUEST
    }
  }
}
