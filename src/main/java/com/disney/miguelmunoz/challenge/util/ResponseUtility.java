package com.disney.miguelmunoz.challenge.util;

import com.disney.miguelmunoz.challenge.exception.ResponseException;
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
  public static ResponseEntity<Void> makeResponse(ResponseException ex) {
    final HttpStatus httpStatus = ex.getHttpStatus();
    log.debug(httpStatus.toString(), ex);
    return new ResponseEntity<>(httpStatus);
  }
  
  public static ResponseEntity<String> makeStringResponse(ResponseException ex) {
    final HttpStatus httpStatus = ex.getHttpStatus();
    log.debug(httpStatus.toString(), ex);
    return new ResponseEntity<>(createMessage(ex), httpStatus);
  }
  
  public static ResponseEntity<String> makeResponseWithId(HttpStatus status, Integer id) {
    return new ResponseEntity<String>(String.format(String.format("id=%s"), id), status );
  }

  public static ResponseEntity<String> makeCreatedResponseWithId(Integer id) {
    return makeResponseWithId(HttpStatus.CREATED, id);
  }

  private static String createMessage(ResponseException ex) {
    return String.format("{\"message\": \"%s\", \"httpStatus\": \"%s\"}", ex.getMessage(), ex.getHttpStatus());
  }
}
