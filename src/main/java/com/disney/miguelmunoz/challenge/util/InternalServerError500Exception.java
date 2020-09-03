package com.disney.miguelmunoz.challenge.util;

import com.disney.miguelmunoz.challenge.exception.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InternalServerErrorException is for server bugs only. It should only be used when the server code throws a 
 * RuntimeException, which should only happen if there's a bug in the server. This should never be used when the 
 * server is intentionally returning a bad result as a result of invalid input.
 * <p>
 * The ResponseUtility.serve() method catches RuntimeExceptions and throws this exception. That's why this class is 
 * not public.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/5/18
 * <p>Time: 1:34 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerError500Exception extends ResponseException {
	/**
	 * Wrap a RuntimeException or other Throwable in an InternalServerErrorException. This should only be used for
	 * unexpected exceptions due to bugs. 
	 * <p>
	 * This single constructor takes a {@code Throwable} because this should always
	 * be to wrap another {@code Throwable}.
	 * @param cause The initial Exception
	 * @see InternalServerError500Exception 
	 */
	InternalServerError500Exception(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
