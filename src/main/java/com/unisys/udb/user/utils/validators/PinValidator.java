package com.unisys.udb.user.utils.validators;

import com.unisys.udb.user.utils.validators.annotation.ValidatePin;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.env.Environment;

import java.util.Objects;

public class PinValidator implements ConstraintValidator<ValidatePin, String> {
    public static final int LENGTH = 6;
    private Environment env;
    private String key;

    public PinValidator(Environment env) {
        this.env = env;
    }

    @Override
    public void initialize(ValidatePin pin) {
        this.key = pin.key();
    }

    @Override
    public boolean isValid(String pin, ConstraintValidatorContext constraintValidatorContext) {

        if (pin.isEmpty() || pin.isBlank()) {
            return pin.matches(Objects.requireNonNull(env.getProperty(this.key)))
                    && pin.length() == LENGTH;
        }
        return true;
    }
}

