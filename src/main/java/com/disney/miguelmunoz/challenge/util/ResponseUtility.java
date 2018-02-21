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
  public static ResponseEntity<Void> makeVoidResponse(Throwable t) {
    if (t instanceof ResponseException) {
      ResponseException ex = (ResponseException) t;
      final HttpStatus httpStatus = ex.getHttpStatus();
      log.debug(httpStatus.toString(), ex);
      return new ResponseEntity<>(httpStatus);
    }
    log.debug(t.getMessage(), t);
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }
  
  public static ResponseEntity<CreatedResponse> makeErrorResponse(Throwable t) {
    if (t instanceof ResponseException) {
      ResponseException ex = (ResponseException) t;
      final HttpStatus httpStatus = ex.getHttpStatus();
      log.debug(httpStatus.toString(), ex);
      return new ResponseEntity<>(makeError(ex), httpStatus);
    }
    log.debug(t.getMessage(), t);
    return new ResponseEntity<>(makeError(t), HttpStatus.BAD_REQUEST);
  }
  
  private static CreatedResponse makeError(Throwable t) {
    CreatedResponse error = new CreatedResponse();
    HttpStatus status;
    if (t instanceof ResponseException) {
      ResponseException ex = (ResponseException) t;
      status = ex.getHttpStatus();
      error.setHttpStatus(status.value());
      error.setMessage(String.format("%s: %s", status.getReasonPhrase(), t.getMessage()));
    } else {
      status = HttpStatus.BAD_REQUEST;
      error.setHttpStatus(status.value());
      error.setMessage(t.getMessage());
    }
    log.debug(String.format("%s: %s", status, error.getMessage()), t);
    return error;
  }

//  public static ResponseEntity<?> makeUnknownProblemResponse(Throwable t) {
//    final HttpStatus httpStatus
//  }

  public static ResponseEntity<Void> makeStatusResponse(HttpStatus status) {
    return new ResponseEntity<>(status);
  }
  
  public static ResponseEntity<CreatedResponse> makeStatusResponse(HttpStatus status, String content) {
    final CreatedResponse response = new CreatedResponse();
    response.setBody(content);
    return new ResponseEntity<>(response, status);
  }
  
  public static ResponseEntity<CreatedResponse> makeCreatedResponseWithId(String id) {
    CreatedResponse response = new CreatedResponse();
    response.setId(id);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  public static <T> ResponseEntity<T> makeCreatedResponse(T body) {
    return new ResponseEntity<>(body, HttpStatus.CREATED);
  }

  private static String createMessage(ResponseException ex) {
    return String.format("{\"message\": \"%s\", \"httpStatus\": \"%s\"}", ex.getMessage(), ex.getHttpStatus());
  }

  private static String createMessage(Throwable ex) {
    return String.format("{\"message\": \"%s\", \"httpStatus\": \"%s\"}", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
