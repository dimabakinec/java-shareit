package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // уникальный идентификатор комментария;

    @Column (nullable = false)
    private String text; // содержимое комментария;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item; // вещь, к которой относится комментарий;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "author_id", updatable = false)
    private User author; // автор комментария;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created; // дата создания комментария.
}