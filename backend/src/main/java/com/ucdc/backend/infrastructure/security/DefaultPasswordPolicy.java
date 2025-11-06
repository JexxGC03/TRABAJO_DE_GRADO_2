package com.ucdc.backend.infrastructure.security;

import com.ucdc.backend.domain.exceptions.logic.BadPasswordException;
import com.ucdc.backend.domain.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultPasswordPolicy implements PasswordPolicy {

    private static final int MIN_LEN = 8;
    private static final int MAX_LEN = 72; // recomendable para bcrypt

    @Override
    public void validate(String raw) {
        List<String> errors = new ArrayList<>();

        if (raw == null) {
            errors.add("password is null");
            throw new BadPasswordException(errors);
        }
        if (raw.length() < MIN_LEN) errors.add("min length " + MIN_LEN);
        if (raw.length() > MAX_LEN) errors.add("max length " + MAX_LEN);
        if (!raw.chars().anyMatch(Character::isUpperCase)) errors.add("at least one uppercase [A-Z]");
        if (!raw.chars().anyMatch(Character::isLowerCase)) errors.add("at least one lowercase [a-z]");
        if (!raw.chars().anyMatch(Character::isDigit))     errors.add("at least one digit [0-9]");
        if (raw.chars().anyMatch(Character::isWhitespace)) errors.add("no spaces allowed");

        // símbolo: cualquier no letra/dígito/espacio (puedes ajustar el set permitido)
        boolean hasSymbol = raw.chars().anyMatch(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c));
        if (!hasSymbol) errors.add("at least one symbol (e.g. !@#$%^&*)");

        // patrones simples a evitar
        String lower = raw.toLowerCase();
        if (lower.contains("password") || lower.contains("123456")) {
            errors.add("avoid common sequences");
        }

        if (!errors.isEmpty()) throw new BadPasswordException(errors);
    }
}
