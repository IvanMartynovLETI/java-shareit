package ru.practicum.shareit.user.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String eMail);

    User findUserById(Long id);
}