package com.example.products.servlet;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador Gson para serializar/deserializar LocalDateTime.
 * 
 * Gson não suporta java.time nativamente, então precisamos
 * deste adaptador para converter LocalDateTime ↔ JSON string.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>,
        JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
                                  JsonSerializationContext context) {
        return new JsonPrimitive(src.format(FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}
