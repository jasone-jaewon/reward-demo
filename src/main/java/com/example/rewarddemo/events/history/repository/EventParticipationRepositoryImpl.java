package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.rewarddemo.events.history.entity.QEventParticipation.eventParticipation;

@RequiredArgsConstructor
public class EventParticipationRepositoryImpl implements EventParticipationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<EventParticipation> findLatestParticipation(String eventId, long memberNo) {
        return jpaQueryFactory
                .selectFrom(eventParticipation)
                .where(
                        eventParticipation.event.id.eq(eventId),
                        eventParticipation.member.no.eq(memberNo)
                )
                .orderBy(eventParticipation.participatedAt.desc())
                .stream().findFirst();
    }
}
