package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.events.entity.Event;

import java.util.List;

public interface EventRepositoryCustom {
    Event findEventWithRewardById(String eventId);

    List<Event> findAllWithReward();
}
