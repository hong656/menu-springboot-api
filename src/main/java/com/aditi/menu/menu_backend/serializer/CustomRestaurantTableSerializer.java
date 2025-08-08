package com.aditi.menu.menu_backend.serializer;

import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CustomRestaurantTableSerializer extends JsonSerializer<RestaurantTable> {

    @Override
    public void serialize(RestaurantTable table, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", table.getId());
        gen.writeNumberField("number", table.getNumber());
        gen.writeEndObject();
    }
}
