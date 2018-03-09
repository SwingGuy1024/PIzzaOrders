package com.disney.miguelmunoz.challenge.util;

import com.disney.miguelmunoz.challenge.exception.ResponseException;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/8/18
 * <p>Time: 3:14 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@FunctionalInterface
public interface ServiceMethod<T> {
  T doService() throws ResponseException;
}
