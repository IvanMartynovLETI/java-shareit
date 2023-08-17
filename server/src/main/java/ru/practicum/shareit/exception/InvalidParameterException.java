package ru.practicum.shareit.exception;

public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String s) {
        super(s);
    }
}