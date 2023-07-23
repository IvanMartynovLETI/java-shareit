package ru.practicum.shareit.constraints;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, BookingDtoRequest> {

    @Override
    public boolean isValid(BookingDtoRequest dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        return dto.getEnd() == null || dto.getStart() == null || dto.getEnd().isAfter(dto.getStart());
    }
}