package com.javali.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordSeguraValidator
        implements ConstraintValidator<PasswordSegura, String> {

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true; // @NotBlank se ocupa de este caso.
        }
        boolean tieneLetra = false;
        boolean tieneNumero = false;
        for (char caracter : password.toCharArray()) {
            if (Character.isLetter(caracter)) {
                tieneLetra = true;
            } else if (Character.isDigit(caracter)) {
                tieneNumero = true;
            }
        }
        return tieneLetra && tieneNumero;
    }
}
