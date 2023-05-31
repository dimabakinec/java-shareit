package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;


import java.time.LocalDateTime;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndItemOwnerId(Long id, Long ownerId);

    @Query("Select distinct b " +
            "From Booking AS b " +
            "join b.item AS i " +
            "Where b.id = ?1 " +
            "and (b.booker.id = ?2 or i.owner.id = ?2)")
    Optional<Booking> findByIdAndBookerIdOrItemOwnerId(Long id, Long bookerId);

    @Query("Select b " +
            "From Booking AS b " +
            "Where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "ORDER BY b.start ASC")
    List<Booking> findByBookerIdAndCurrentMomentBetweenStartAndEnd(Long bookerId, LocalDateTime currentMoment);

    @Query("Select b " +
            "From Booking AS b " +
            "join b.item AS i " +
            "Where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd(Long ownerId, LocalDateTime currentMoment);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(Long id, Long bookerId, BookingStatus status,
                                                                 LocalDateTime createdDate);

    @Query("Select b " +
            "From Booking AS b " +
            "Where b.item.id = ?1 " +
            "and (b.start >= ?2 and b.end <= ?3)")
    List<Booking> getBookingDate(Long id, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime currentMoment, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime currentMoment, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long userId, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long userId, LocalDateTime currentMoment, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long userId, LocalDateTime currentMoment, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdIn(Set<Long> itemId, Sort sort);
}