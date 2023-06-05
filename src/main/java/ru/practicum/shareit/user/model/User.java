package ru.practicum.shareit.user.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email")
        }
)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // уникальный идентификатор пользователя;

    private String name; // имя или логин пользователя;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String email; // адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
}