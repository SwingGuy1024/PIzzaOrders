package com.disney.miguelmunoz.challenge.serial;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.disney.miguelmunoz.challenge.entities.PojoUtility;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

/**
 * This class was suggested by http://www.baeldung.com/jackson-serialize-dates to solve my date serialization problem.
 * It's returning values in the server format instead of the client format.
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date> {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(PojoUtility.TIME_FORMAT);

  @Override
  public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    String formattedDate = dateFormat.format(date);
    gen.writeString(formattedDate);
  }
}