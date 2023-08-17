package ru.practicum.shareit.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    private String text;
    private String authorName;
    private Long itemId;
    private String itemName;
    private LocalDateTime created;
}