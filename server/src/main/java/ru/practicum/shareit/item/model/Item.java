package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Table(name = "items")
@ToString
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // уникальный идентификатор вещи;

    @Column(nullable = false)
    private String name; // краткое название;

    @Column(nullable = false)
    private String description; // развёрнутое описание;

    @Column(
            name = "is_available",
            nullable = false
    )
    private Boolean available; // статус о том, доступна или нет вещь для аренды;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "owner_id")
    private User owner; // владелец вещи;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    private ItemRequest request; // ссылка на соответствующий запрос (заполняется только если вещь создана по запросу).
}