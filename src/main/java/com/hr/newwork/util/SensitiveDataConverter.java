package com.hr.newwork.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.newwork.data.entity.SensitiveData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// JSONB converter
@Converter(autoApply = true)
public class SensitiveDataConverter implements AttributeConverter<SensitiveData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SensitiveData attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing SensitiveData to JSON", e);
        }
    }

    @Override
    public SensitiveData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, SensitiveData.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing JSON to SensitiveData", e);
        }
    }
}
