package com.ucdc.backend.infrastructure.persistence.converter;

import com.ucdc.backend.domain.value.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {
    @Override
    public String convertToDatabaseColumn(Email attribute) {
        return (attribute != null) ? attribute.value() : null;
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return (dbData != null) ? new Email(dbData) : null;
    }
}
