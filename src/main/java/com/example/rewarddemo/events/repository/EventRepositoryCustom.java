package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.events.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryCustom {
    Optional<Event> findEventWithRewardById(String eventId);

    List<Event> findAllWithReward();
}
