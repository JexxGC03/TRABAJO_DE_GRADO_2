package com.ucdc.backend.domain.exceptions.logic;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super("La contraseña actual es incorrecta");
    }
}
