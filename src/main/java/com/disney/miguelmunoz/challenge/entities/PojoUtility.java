package com.disney.miguelmunoz.challenge.entities;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import com.disney.miguelmunoz.challenge.exception.ResponseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * By convention, all methods that may throw a ResponseException begin with the word confirm
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/11/18
 * <p>Time: 10:26 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"UnusedReturnValue", "HardCodedStringLiteral"})
public enum PojoUtility {
  ;

  private static final Logger log = LoggerFactory.getLogger(PojoUtility.class);
  private static final ObjectMapper mapper = new ObjectMapper();
  public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
  private static final DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.UK);

  /**
   * Clones the date. If the date is null, returns null. (This doesn't actually call clone().) This is for Date values
   * that are allowed to be null, so it may return null. Never throws a NullPointerException.
   * @param theDate The Date to clone
   * @return A clone of the specified Date.
   */
  public static Date cloneDate(Date theDate) {
    if (theDate != null) {
      // clone() doesn't work on java.sql.Date. This works fine.
      return new Date(theDate.getTime());
    }
    return null;
  }

  private static final Iterable<Object> emptyIterable = Collections.unmodifiableCollection(Collections.emptyList());

  /**
   * Returns the provided collection. If the collection is null, returns an unmodifiable empty List. This lets you
   * iterate over any collection without checking it for null: 
   * <p>
   * {@code for (String s: skipNull(maybeNullSetOfStrings)) {...}}
   * @param iterable The collection or other Iterable
   * @param <T> The type of the collection members
   * @return the supplied Iterable, or if it's null, an unmodifiable empty Iterable.
   */
  @SuppressWarnings("unchecked") // always empty, so there are no values to cast incorrectly.
  public static <T> Iterable<T> skipNull(Iterable<T> iterable) {
    if (iterable == null) {
      //noinspection AssignmentOrReturnOfFieldWithMutableType
      return (Iterable<T>) emptyIterable;
    }
    return iterable;
  }

  /**
   * Converts the String Id value to a Integer, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Integer value
   * @throws ResponseException BAD_REQUEST (400) if id is null or is not readable as a long value.
   */
  public static Integer confirmAndDecodeInteger(final String id) throws ResponseException {
    try {
      @SuppressWarnings("argument.type.incompatible") final Integer longValue = Integer.valueOf(id);
      return longValue; // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw ResponseException.unreadableId(id, e);
    }
  }

  /**
   * Converts the String Id value to a Long, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Long value
   * @throws ResponseException BAD_REQUEST (400) if id is null or is not readable as a long value.
   */
  public static Long confirmAndDecodeLong(final String id) throws ResponseException {
    try {
      @SuppressWarnings("argument.type.incompatible") final Long longValue = Long.valueOf(id);
      return longValue; // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw ResponseException.unreadableId(id, e);
    }
  }

  /**
   * Use when an entity was not found in the database at the specified id. 
   * @param entity The entity to test.
   * @param id The id, used to generate a useful error message
   * @param <T> The type of the entity
   * @return entity, if it's not null
   * @throws ResponseException NOT_FOUND (404) if entity is null.
   */
  public static <T> T confirmNotNull(T entity, Object id) throws ResponseException {
    if (entity == null) {
      throw new ResponseException(HttpStatus.NOT_FOUND, String.format("Missing object at id %s", id));
    }
    return entity;
  }

  /**
   * Use when a non-entity object should not be null. Returns a BadRequest (400). If an entity is null, you should use
   * confirmNotNull(T object, Object id), which return a NOT_FOUND (404).
   * @param object The non-entity object to test.
   * @param <T> The object type
   * @return object, only if it's not null
   * @throws ResponseException BAD_REQUEST (400) if object is null
   */
  public static <T> T confirmNeverNull(T object) throws ResponseException {
    if (object == null) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing object");
    }
    return object;
  }

  /**
   * Use when a value should be null. For example, if a field should not be initialized, such as the ID of an entity 
   * that is about to be created, or an end-time for an operation that has not yet ended.
   * @param object The object that should be null.
   * @throws ResponseException BAD_REQUEST (400) if the object is not null.
   */
  public static void confirmNull(Object object) throws ResponseException {
    if (object != null) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "non-null value");
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws ResponseException BAD_REQUEST (400) if the objects are not equal
   * @see Objects#equals(Object, Object) 
   */
  public static <T> void confirmEqual(T expected, T actual) throws ResponseException {
    if (!Objects.equals(actual, expected)) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, String.format("Expected %s  Found %s", expected, actual));
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param message The message to use if the objects are not equal
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws ResponseException BAD_REQUEST (400) if the objects are not equal
   * @see Objects#equals(Object, Object)
   */
  public static <T> void confirmEqual(String message, T expected, T actual) throws ResponseException {
    if (!Objects.equals(actual, expected)) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, message);
    }
  }

  public static <T> List<LinkedHashMap<String, ?>> convertEntities(String json) throws IOException {
    return mapper.readValue(json, new TypeReference<List<T>>() { });
  }
  
  public static <I, O> List<O> convertList(Collection<I> inputList) {
    return mapper.convertValue(inputList, new TypeReference<List<O>>() { });
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
   * The return value is usually not used, since this is just to test for valid data.
   * @param s The String
   * @return The original String, or an empty String if the original was empty. Never returns null.
   */
  public static String notNull(String s) {
    return (s == null) ? "" : s;
  }

  /**
   * Returns the String. Throws a ResponseException if the String is null or empty. 
   * The return value is usually not used, since this is just to test for valid data.
   * @param s The String
   * @return s
   * @throws ResponseException BAD_REQUEST (400) if the String is null or empty
   */
  public static String confirmNotEmpty(String s) throws ResponseException {
    if ((s == null) || s.isEmpty()) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, String.format("Null or empty value: \"%s\"", s));
    }
    return s;
  }
}
