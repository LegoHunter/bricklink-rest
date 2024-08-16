package com.bricklink.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public final class DateUtils {
    public static final ZoneId PST = ZoneId.of("America/Los_Angeles");

    public static Date toDate(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                       .map(ldt -> Date.from(ldt.atZone(PST)
                                                .toInstant()))
                       .orElse(null);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return Optional.ofNullable(date)
                       .map(d -> date.toInstant().atZone(PST).toLocalDateTime())
                       .orElse(null);
    }

    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                       .map(d -> ZonedDateTime.of(localDateTime, PST))
                       .orElse(null);
    }

    public static class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
        @Override
        public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(DateTimeFormatter.ISO_INSTANT.format(zonedDateTime));
        }
    }

    public static class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser,
                                         DeserializationContext deserializationContext)
                throws IOException {

            LocalDateTime localDateTime = LocalDateTime.parse(
                    jsonParser.getText(),
                    DateTimeFormatter.ISO_ZONED_DATE_TIME);

            return ZonedDateTime.of(localDateTime, PST);
        }
    }
}
