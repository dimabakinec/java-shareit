package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // уникальный идентификатор запроса;

    @Column(nullable = false)
    private String description; // текст запроса, содержащий описание требуемой вещи;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requester_id")
    private User requester; // пользователь, создавший запрос;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now(); // дата и время создания запроса.
}