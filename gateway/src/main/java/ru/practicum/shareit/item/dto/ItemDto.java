package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id; // уникальный идентификатор вещи;

    @NotBlank
    private String name; // краткое название;

    @NotBlank
    private String description; // развёрнутое описание;

    @NotNull
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private Long requestId; // id запроса (заполняется только если вещь создана по запросу).
}