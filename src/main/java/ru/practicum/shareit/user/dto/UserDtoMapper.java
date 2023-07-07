package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class UserDtoMapper {
    public Optional<UserDto> userToUserDto(User user) {
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertUserToUserDto(user));
        }
    }

    public User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public Optional<List<UserDto>> usersToDtos(Collection<User> users) {
        if (users == null) {
            return Optional.empty();
        } else {
            return Optional.of(users
                    .stream()
                    .map(this::convertUserToUserDto)
                    .collect(Collectors.toList()));
        }
    }

    public UserDto convertUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }
}