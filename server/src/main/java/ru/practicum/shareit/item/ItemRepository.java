package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerId(Long ownerId, Pageable page);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    @Query(" select i from Item i" +
            " where (upper(i.name) like upper(concat('%', ?1, '%'))" +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.available = true")
    Page<Item> search(String text, Pageable page);

    List<Item> findByRequestIdIn(Set<Long> requestId);

    List<Item> findByRequestId(Long requestId);
}
