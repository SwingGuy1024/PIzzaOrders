package com.disney.miguelmunoz.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/5/18
 * <p>Time: 1:34 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends ResponseException {
	public InternalServerErrorException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
