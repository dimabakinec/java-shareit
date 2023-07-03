package ru.practicum.shareit.user.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name; // имя или логин пользователя;
    @EqualsAndHashCode.Include
    private String email; // адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
}