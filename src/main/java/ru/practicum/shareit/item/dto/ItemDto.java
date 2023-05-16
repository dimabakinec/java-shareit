package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemDto {
    private long id; // уникальный идентификатор вещи;
    @NotBlank
    private String name; // краткое название;
    @NotBlank
    private String description; // развёрнутое описание;
    @NotNull
    private Boolean available; // статус о том, доступна или нет вещь для аренды;
    private String request; // ссылка на соответствующий запрос (заполняется только если вещь создана по запросу).
}