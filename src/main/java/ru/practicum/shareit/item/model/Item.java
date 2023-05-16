package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Data
public class Item {

    private long id; // уникальный идентификатор вещи;
    private String name; // краткое название;
    private String description; // развёрнутое описание;
    private Boolean available; // статус о том, доступна или нет вещь для аренды;
    private long owner; // владелец вещи;
    private String request; // ссылка на соответствующий запрос (заполняется только если вещь создана по запросу).
}