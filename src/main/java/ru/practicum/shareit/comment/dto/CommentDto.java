package ru.practicum.shareit.comment.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @NotNull
    @NotEmpty
    private String text;

    private String authorName;
    private Long itemId;
    private String itemName;
    private LocalDateTime created;
}