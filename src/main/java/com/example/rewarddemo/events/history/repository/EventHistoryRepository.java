package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {
    Optional<EventHistory> findByMemberNoAndEventIdAndParticipateDate(
            Long memberNo,
            String eventId,
            LocalDate participateDate
    );

    Page<EventHistory> findHistoriesByEventIdAndParticipateDate(String eventId, LocalDate participateDate, Pageable pageable);
}
