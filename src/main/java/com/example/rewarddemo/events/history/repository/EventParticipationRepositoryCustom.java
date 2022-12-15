package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventParticipation;

import java.util.Optional;

public interface EventParticipationRepositoryCustom {
    Optional<EventParticipation> findLatestParticipation(String eventId, long memberNo);
}
