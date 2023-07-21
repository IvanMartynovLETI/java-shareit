package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @NotNull(message = "User's login shouldn't be an empty.")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Email(message = "Incorrect email")
    @NotNull(message = "User's email shouldn't be an empty")
    @Column(name = "email", length = 20, nullable = false, unique = true)
    private String email;
}