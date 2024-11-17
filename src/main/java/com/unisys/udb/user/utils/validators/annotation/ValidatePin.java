package com.unisys.udb.user.utils.validators.annotation;

import com.unisys.udb.user.utils.validators.PinValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = PinValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidatePin {
    String key() default "";

    String message() default "Should not be null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
