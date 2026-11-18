package com.javali.validators;

import com.javali.dtos.UsuarioRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordSeguraValidatorTest {

    private final PasswordSeguraValidator validator =
            new PasswordSeguraValidator();

    @Test
    void aceptaLetrasYNumeros() {
        assertTrue(validator.isValid("Clave1234", null));
    }

    @Test
    void rechazaSoloNumerosOSoloLetras() {
        assertFalse(validator.isValid("12345678", null));
        assertFalse(validator.isValid("soloLetras", null));
    }

    @Test
    void dejaLosValoresVaciosParaNotBlank() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid(" ", null));
    }

    @Test
    void funcionaDentroDeUsuarioRequest() {
        Validator beanValidator =
                Validation.buildDefaultValidatorFactory().getValidator();

        UsuarioRequest sinLetras = new UsuarioRequest(
                "Ana", "ana@javali.com", "12345678");
        UsuarioRequest correcta = new UsuarioRequest(
                "Ana", "ana@javali.com", "Clave1234");

        assertFalse(beanValidator.validate(sinLetras).isEmpty());
        assertTrue(beanValidator.validate(correcta).isEmpty());
    }
}
