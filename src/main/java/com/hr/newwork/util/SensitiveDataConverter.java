package com.hr.newwork.util;

import com.hr.newwork.data.entity.SensitiveData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// JSONB converter
@Converter(autoApply = true)
public class SensitiveDataConverter implements AttributeConverter<SensitiveData, String> {
    @Override
    public String convertToDatabaseColumn(SensitiveData attribute) {
        // Implement JSON serialization (e.g., using Jackson)
        return null;
    }

    @Override
    public SensitiveData convertToEntityAttribute(String dbData) {
        // Implement JSON deserialization
        return null;
    }
}
