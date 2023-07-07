package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(message = "User's login shouldn't be an empty.")
    private String name;

    @Email(message = "Incorrect email")
    @NotNull(message = "User's email shouldn't be an empty")
    private String email;
}