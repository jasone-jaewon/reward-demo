package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {
}
