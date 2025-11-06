package com.ucdc.backend.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProviderValidator.class)
public @interface ValidProvider {
    String message() default "Invalid provider name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
