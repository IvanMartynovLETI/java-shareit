package ru.practicum.shareit.user.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String eMail);

    User findUserById(Long id);
}