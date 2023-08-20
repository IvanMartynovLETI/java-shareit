package ru.practicum.shareit.constraints;

import ru.practicum.shareit.user.dto.UserDtoForUpdating;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;

public class UserUpdateValidator implements ConstraintValidator<UserUpdate, UserDtoForUpdating> {

    @Override
    public boolean isValid(UserDtoForUpdating dto, ConstraintValidatorContext context) {
        boolean isUpdateValid = true;

        if (dto != null) {
            if (dto.getName() == null && dto.getEmail() == null) {
                isUpdateValid = false;
            } else {
                if (dto.getName() == null) {
                    try {
                        @Email String email = dto.getEmail();
                    } catch (ConstraintViolationException e) {
                        isUpdateValid = false;
                    }
                }
            }
        }

        return isUpdateValid;
    }
}
