package com.disney.miguelmunoz.framework;

import java.lang.annotation.Annotation;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.persistence.Entity;
import com.disney.miguelmunoz.framework.exception.BadRequest400Exception;
import com.disney.miguelmunoz.framework.exception.NotFound404Exception;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

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
	//	private static final Duration THREE_DAYS = Duration.ofDays(3L);
  public static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

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
      return (Iterable<T>) emptyIterable;
    }
    return iterable;
  }

  /**
   * Converts the String Id value to a Integer, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Integer value
   * @throws BadRequest400Exception if id is null or is not readable as an int value.
   */
  public static Integer confirmAndDecodeInteger(final String id) throws BadRequest400Exception {
    try {
      return Integer.valueOf(id); // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw new BadRequest400Exception(id, e);
    }
  }

  /**
   * Converts the String Id value to a Long, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Long value
   * @throws BadRequest400Exception if id is null or is not readable as a long value.
   */
  public static Long confirmAndDecodeLong(final String id) throws BadRequest400Exception {
    try {
      return Long.valueOf(id); // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw new BadRequest400Exception(id, e);
    }
  }

  /**
   * Returns a {@literal Supplier<NotFound404Exception>} for use in an {@code orElseThrow()} method, with the specified id as the
   * exception's message.
   * 
   * <pre>
   *   Thing thing = thingRepository.findOne(id).orElseThrow(() -> new NotFound404Exception(id.toString()));
   * </pre>
   * becomes
   * 
   * <pre>
   *   Thing thing = thingRepository.findOne(id).orElseThrow(notFound(id));
   * </pre>
   * This may all be encapsulated further using the findOrThrow() method:
   * <pre>
   *   Thing thing = findOrThrow(thingRepository, id);
   * </pre>
   * @param id The id for the message
   * @param <ID> The type of the id
   * @return a {@literal Supplier<NotFound404Exception>} which puts the ID in the message.
   * @see #findOrThrow
   */
  public static <ID> Supplier<NotFound404Exception> notFound(ID id) {
    return () -> new NotFound404Exception(String.format("Missing entity at id %s", id));
  }

  /**
   * Confirms the requested entity was found, throwing a NotFound404Exception if not.
   * Use when an entity was requested from the database at the specified id. 
   * @param entity The entity to test.
   * @param id The id, used to generate a useful error message
   * @param <T> The type of the entity
   * @return entity, if it's not null
   * @throws NotFound404Exception if entity is null.
   */
  @SuppressWarnings("ConstantConditions")
  public static <T> T confirmEntityFound(T entity, Object id) throws NotFound404Exception {
    if (entity == null) {
      throw notFound(id).get();
    }
    assert isEntityAssertion(entity) : "This method is only for entities. Use confirmNeverNull()";
    return entity;
  }

  /**
   * Retrieves the entity of type T, with the specified id, from the specified repository. If no such entity exists, throws a
   * NotFound404Exception exception with the id as the message.
   *
   * @param repository The repository.
   * @param id   The id of the entity to retrieve
   * @param <T>  The type of the entity
   * @param <ID> The type of the entity's ID
   * @return The entity from the repository.
   * @throws NotFound404Exception if the entity does not exist in the repository.
   */
  public static <T, ID> T findOrThrow(CrudRepository<T, ID> repository, ID id) throws NotFound404Exception {
    return repository
        .findById(id)
        .orElseThrow(notFound(id));
  }

  /**
   * Confirms the requested entity was found, throwing a NotFound404Exception if not.
   * Use when an entity was added to another entity, and wasn't explicitly retrieved from an id.
   *
   * @param entity The entity to test.
   * @param <T>    The type of the entity
   * @return entity, if it's not null
   * @throws NotFound404Exception if entity is null.
   */
  public static <T> T confirmEntityFound(T entity) throws NotFound404Exception {
    if (entity == null) {
      throw new NotFound404Exception("Missing object");
    }
    assert isEntityAssertion(entity) : "This method is only for entities. Use confirmNeverNull()";
    return entity;
  }

  /**
   * Use when a non-entity object should not be null. Throws a BadRequest400Exception if null. If testing for an entity, you 
   * should use confirmFound(T object, Object id), which return a NOT_FOUND (404).
   * @param object The non-entity object to test.
   * @param <T> The object type
   * @return object, only if it's not null
   * @throws BadRequest400Exception if object is null
   */
  public static <T> T confirmNeverNull(T object) throws BadRequest400Exception {
    if (object == null) {
      throw new BadRequest400Exception("Missing object");
    }
    assert !isEntityAssertion(object) : String.format("This method is not for entity objects. Use confirmFound(): %s", object.getClass());
    return object;
  }
  
  private static boolean isEntityAssertion(Object object) {
    Set<Annotation> annotationSet = new HashSet<>();
    return Arrays.stream(object.getClass().getDeclaredAnnotations())
        .anyMatch(a -> a.annotationType() == Entity.class);
  }
  
  public static <T> Set<T> asSet(T[] tArray) {
    return new HashSet<>(Arrays.asList(tArray));
  }

  /**
   * Use when a value should be null. For example, if a field should not be initialized, such as the ID of an entity 
   * that is about to be created, or an end-time for an operation that has not yet ended.
   * @param object The object that should be null.
   * @throws BadRequest400Exception if the object is not null.
   */
  public static void confirmNull(Object object) throws BadRequest400Exception {
    if (object != null) {
	    //noinspection StringConcatenation
	    throw new BadRequest400Exception("non-null value: " + object);
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws BadRequest400Exception if the objects are not equal
   * @see Objects#equals(Object, Object) 
   */
  public static <T> void confirmEqual(T expected, T actual) throws BadRequest400Exception {
    if (!Objects.equals(actual, expected)) {
      throw new BadRequest400Exception(String.format("Expected %s  Found %s", expected, actual));
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param message The message to use if the objects are not equal
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws BadRequest400Exception if the objects are not equal
   * @see Objects#equals(Object, Object)
   */
  public static <T> void confirmEqual(String message, T expected, T actual) throws BadRequest400Exception {
    if (!Objects.equals(actual, expected)) {
      throw new BadRequest400Exception(message);
    }
  }

//  // This doesn't get used, and the missing LinkedHashMap from the return statement suggests it can't work this way,
//  // so the whole thing has been removed. We probably don't need it, but I'm not entirely sure, so I'm keeping it 
//  // around until I have a clearer idea if we will need it.
//  public static <T> List<LinkedHashMap<String, ?>> convertEntities(String json) throws IOException {
//    return mapper.readValue(json, new TypeReference<List<T>>() { });
//  }

  /**
   * Convert a Collection of DTOs into a collection of the corresponding entities.
   * @param inputList The list of DTOs
   * @param <I> The Input DTO type
   * @param <O> The Output entity type
   * @return A list of entities of type O
   */
  public static <I, O> List<O> convertList(Collection<I> inputList) {
    return mapper.convertValue(inputList, new TypeReference<List<O>>() { });
  }

//  private static Instant parse(String dateText) {
//    if (dateText == null) {
//      return null;
//    }
//    try {
//      return Instant(timeFormat.parse(dateText).getTime());
//    } catch (ParseException e) {
//      throw new IllegalStateException(String.format("Failed to parse %s", dateText), e);
//    }
//  }

  /**
   * Returns the String, or an empty String if the String is null.
   * @param s The String
   * @return The original String, or an empty String if the original was empty. Never returns null.
   */
  public static String notNull(String s) {
    return (s == null) ? "" : s;
  }

  /**
   * Returns the String. Throws a BadRequest400Exception if the String is null or empty. 
   * The return value is usually not used, since this is just to test for valid data.
   * @param s The String
   * @return s
   * @throws BadRequest400Exception if the String is null or empty
   */
  public static String confirmNotEmpty(String s) throws BadRequest400Exception {
    if ((s == null) || s.isEmpty()) {
      throw new BadRequest400Exception(String.format("Null or empty value: \"%s\"", s));
    }
    return s;
  }
}
