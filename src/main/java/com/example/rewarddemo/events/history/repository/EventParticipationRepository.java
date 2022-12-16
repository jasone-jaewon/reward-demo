package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventParticipation;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.Optional;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long>, EventParticipationRepositoryCustom {
    Optional<EventParticipation> findByMemberNoAndEventIdAndParticipateDate(
            Long memberNo,
            String eventId,
            LocalDate participateDate
    );

    Page<EventParticipation> findAllByEventIdAndParticipateDate(String eventId, LocalDate participateDate, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Long countByEventIdAndParticipateDate(String eventId, LocalDate participateDate);
}
