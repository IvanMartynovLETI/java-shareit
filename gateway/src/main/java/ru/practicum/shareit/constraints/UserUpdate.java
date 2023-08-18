package ru.practicum.shareit.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserUpdateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserUpdate {
    String message() default "User update is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
