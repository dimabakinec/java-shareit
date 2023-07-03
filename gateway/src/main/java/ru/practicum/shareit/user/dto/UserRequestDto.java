package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    private Long id;
    private String name; // имя или логин пользователя;

    @NotBlank
    @Email
    private String email; // адрес электронной почты.
}