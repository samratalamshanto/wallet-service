package com.samratalam.ewallet_system.custom.annotation;

import com.samratalam.ewallet_system.custom.validator.StrongPasswordValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Invalid Password. Password must be 8 characters long.";
}
