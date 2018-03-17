package com.disney.miguelmunoz.challenge.exception;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

/**
 * Thrown by a service implementation with an HttpStatus instance. This Exception lets the service specify the 
 * HttpStatus that should be returned to the client. This allows the code that throws the exception to communicate
 * with the catch clause that generates the error response. All Constructors of this method require a non-null 
 * HttpStatus value and a message to return to the client. This also has static factory classes for common cases.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/11/18
 * <p>Time: 4:02 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class ResponseException extends Exception {
  private final HttpStatus httpStatus;

  /**
   * Create a ResponseException with the specified status and error message
   * @param status the status
   * @param message the message
   */
  public ResponseException(HttpStatus status, String message) {
    super(message);
    httpStatus = status;
  }

  /**
   * Create a ResponseException with the specified status, error message, and cause
   * @param status the status
   * @param message the message
   * @param cause the cause
   */
  @SuppressWarnings("WeakerAccess")
  public ResponseException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    httpStatus = status;
  }

  /**
   * Return a ResponseException with the BAD_REQUEST status when passed a bad id.  
   * @param idText The value of the requested ID, as a String
   * @param cause The Exception or Error that identifies the problem
   * @return A ResponseException with a BAD_REQUEST status
   */
  public static ResponseException unreadableId(String idText, Throwable cause) {
    return new ResponseException(HttpStatus.BAD_REQUEST, Objects.toString(idText), cause);
  }

  /**
   * Return a ResponseException with a NOT_FOUND status when the resource with the specifed ID is not found
   * @param titleId The ID of the requested resource
   * @return A ResponseException with a NOT_FOUND status
   */
  public static ResponseException ownerNotFound(Long titleId) {
    return new ResponseException(HttpStatus.NOT_FOUND, titleId.toString());
  }

  @JsonIgnore
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
  
}
