package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;  // уникальный идентификатор бронирования;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; // дата и время начала бронирования;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; // дата и время конца бронирования;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item; // вещь, которую пользователь бронирует;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "booker_id")
    private User booker; // пользователь, который осуществляет бронирование;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status; // статус бронирования.
}