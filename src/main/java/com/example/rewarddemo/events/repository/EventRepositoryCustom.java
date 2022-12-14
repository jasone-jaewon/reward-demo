package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.events.entity.Event;

public interface EventRepositoryCustom {
    Event findEventWithRewardById(String eventId);
}
