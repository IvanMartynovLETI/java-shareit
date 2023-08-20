package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraints.UserUpdate;

@Getter
@Setter
@AllArgsConstructor
@UserUpdate
public class UserDtoForUpdating {
    private Long id;
    private String name;
    private String email;
}