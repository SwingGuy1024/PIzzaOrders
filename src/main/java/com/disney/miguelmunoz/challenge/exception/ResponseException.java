package com.disney.miguelmunoz.challenge.exception;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/11/18
 * <p>Time: 4:02 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class ResponseException extends Exception {
  private static final Logger log = LoggerFactory.getLogger(ResponseException.class);
  private final HttpStatus httpStatus;
  
  public ResponseException(HttpStatus status, String message) {
    super(message);
    httpStatus = status;
  }

  public ResponseException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    httpStatus = status;
  }

  public static ResponseException unreadableId(String idText, Throwable cause) {
    return new ResponseException(HttpStatus.BAD_REQUEST, Objects.toString(idText), cause);
  }
  
  public static ResponseException ownerNotFound(Long titleId) {
    return new ResponseException(HttpStatus.NOT_FOUND, titleId.toString());
  }

  @JsonIgnore
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
  
}
