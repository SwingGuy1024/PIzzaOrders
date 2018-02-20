package com.disney.miguelmunoz.challenge.entities;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/11/18
 * <p>Time: 10:26 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum PojoUtility {
  ;

  private static final Logger log = LoggerFactory.getLogger(PojoUtility.class);
  private static final ObjectMapper mapper = new ObjectMapper();
  public static final String TIME_FORMAT = "yyyy-MM-dd HH-mm-ss";
  private static final DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.UK);

  /**
   * Clones the date. If the date is null, returns null. (Doesn't actually call clone().)
   * @param theDate The date to clone
   * @return A clone of the specified Date.
   */
  public static Date cloneDate(Date theDate) {
    if (theDate != null) {
      // clone() doesn't work on java.sql.Date. This works fine.
      return new Date(theDate.getTime());
    }
    return null;
  }

  private static final Collection<Object> emptyIterable = Collections.unmodifiableCollection(Collections.emptyList());

  /**
   * Returns the provided collection. If the collection is null, returns an unmodifiable empty List. This lets you
   * iterate over any collection without checking it for null: 
   * <p>
   * {@code for (String s: skipNull(maybeNullSetOfStrings)) {...}}
   * @param iterable The collection
   * @param <T> The type of the collection members
   * @return the supplied collection, or if it's null, an unmodifiable empty list.
   */
  @SuppressWarnings("unchecked") // always empty, so there are no values to cast incorrectly.
  public static <T> Collection<T> skipNull(Collection<T> iterable) {
    if (iterable == null) {
      //noinspection AssignmentOrReturnOfFieldWithMutableType
      return (Collection<T>) emptyIterable;
    }
    return iterable;
  }

  /**
   * Converts the String Id value to a Long, throwing an exception if it can't.
   * @param id The id as a String
   * @return the id as a Long value
   * @throws ResponseException if id is null or is not readable as a long value, throws a Bad Request response.
   */
  public static Long decodeIdString(final String id) throws ResponseException {
    try {
      @SuppressWarnings("argument.type.incompatible")
      final Long longValue = Long.valueOf(id);
      return longValue; // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw ResponseException.unreadableId(id, e);
    }
  }

  private static <T> T notNull(T title) throws ResponseException {
    if (title == null) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing title");
    }
    return title;
  }

  public static <T> List<LinkedHashMap<String, ?>> convertTitles(String json) throws IOException {
    return mapper.readValue(json, new TypeReference<List<T>>() { });
  }

  private static Date parse(String dateText) {
    if (dateText == null) {
      return null;
    }
    try {
      return new Date(timeFormat.parse(dateText).getTime());
    } catch (ParseException e) {
      throw new IllegalStateException(String.format("Failed to parse %s", dateText), e);
    }
  }

  /**
   * Returns the String, or an empty String if the String is null.
   * @param s The String
   * @return The original String, or an empty String if the original was empty. Never returns null.
   */
  public static String notNull(String s) {
    return (s == null) ? "" : s;
  }
  
  public static String notEmpty(String s) throws ResponseException {
    if ((s == null) || s.isEmpty()) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Null or empty value");
    }
    return s;
  }
}
