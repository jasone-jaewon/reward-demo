package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.rewarddemo.events.history.entity.QEventHistory.eventHistory;

@RequiredArgsConstructor
public class EventHistoryRepositoryImpl implements EventHistoryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<EventHistory> findLatestHistory(String eventId, long memberNo) {
        return jpaQueryFactory
                .selectFrom(eventHistory)
                .where(
                        eventHistory.event.id.eq(eventId),
                        eventHistory.member.no.eq(memberNo)
                )
                .orderBy(eventHistory.participatedAt.desc())
                .stream().findFirst();
    }
}
