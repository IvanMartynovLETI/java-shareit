package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class UserDtoMapper {
    public UserDto userToUserDto(User user) {

        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User userDtoToUser(UserDto userDto) {

        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public List<UserDto> usersToDtos(Collection<User> users) {
        return users
                .stream()
                .map(this::userToUserDto)
                .collect(Collectors.toList());
    }
}