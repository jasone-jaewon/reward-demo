package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
}
