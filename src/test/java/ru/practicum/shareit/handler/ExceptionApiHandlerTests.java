package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityUnavailableException;
import ru.practicum.shareit.exception.InvalidParameterException;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExceptionApiHandlerTests {
    @InjectMocks
    ExceptionApiHandler exceptionApiHandler;

    @Test
    public void entityAlreadyExistsCaseTest() {
        String errorStr = "Entity already exists.";
        HttpStatus status = HttpStatus.CONFLICT;
        ResponseEntity<ErrorMessage> entity = exceptionApiHandler.entityAlreadyExistsException(
                new EntityAlreadyExistsException(errorStr));

        assertThat(Objects.requireNonNull(entity.getBody()).getError()).isEqualTo(errorStr);
        assertThat(entity.getStatusCode()).isEqualTo(status);
    }

    @Test
    public void entityDoesNotExistCaseTest() {
        String errorStr = "Entity doesn't exist.";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseEntity<ErrorMessage> entity = exceptionApiHandler.entityDoesNotExistException(
                new EntityDoesNotExistException(errorStr));

        assertThat(Objects.requireNonNull(entity.getBody()).getError()).isEqualTo(errorStr);
        assertThat(entity.getStatusCode()).isEqualTo(status);
    }

    @Test
    public void entityUnavailableCaseTest() {
        String errorStr = "Entity unavailable.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseEntity<ErrorMessage> entity = exceptionApiHandler.entityUnavailableException(
                new EntityUnavailableException(errorStr));

        assertThat(Objects.requireNonNull(entity.getBody()).getError()).isEqualTo(errorStr);
        assertThat(entity.getStatusCode()).isEqualTo(status);
    }

    @Test
    public void invalidParameterCaseTest() {
        String errorStr = "Invalid parameter.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseEntity<ErrorMessage> entity = exceptionApiHandler.invalidParameterException(
                new InvalidParameterException(errorStr));

        assertThat(Objects.requireNonNull(entity.getBody()).getError()).isEqualTo(errorStr);
        assertThat(entity.getStatusCode()).isEqualTo(status);
    }
}