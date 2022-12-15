package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long>, EventParticipationRepositoryCustom {
    Optional<EventParticipation> findByMemberNoAndEventIdAndParticipateDate(
            Long memberNo,
            String eventId,
            LocalDate participateDate
    );

    Page<EventParticipation> findAllByEventIdAndParticipateDate(String eventId, LocalDate participateDate, Pageable pageable);

    Long countByEventIdAndParticipateDate(String eventId, LocalDate participateDate);
}
