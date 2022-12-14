package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventHistory;

import java.util.Optional;

public interface EventHistoryRepositoryCustom {
    Optional<EventHistory> findLatestHistory(String eventId, long memberNo);
}
