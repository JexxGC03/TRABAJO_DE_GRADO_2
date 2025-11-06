package com.ucdc.backend.infrastructure.web.validation;

import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.domain.exceptions.logic.InvalidProviderException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProviderValidator implements ConstraintValidator<ValidProvider, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;
        try {
            Provider.valueOf(s.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            throw  new InvalidProviderException(s);
        }
    }
}
