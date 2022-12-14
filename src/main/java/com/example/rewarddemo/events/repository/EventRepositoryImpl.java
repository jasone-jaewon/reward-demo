package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.events.entity.Event;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.example.rewarddemo.events.entity.QEvent.event;
import static com.example.rewarddemo.events.reward.entity.QBonusReward.bonusReward;
import static com.example.rewarddemo.events.reward.entity.QReward.reward;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Event findEventWithRewardById(String eventId) {
        return jpaQueryFactory
                .select(event)
                .from(event)
                .join(event.reward, reward).fetchJoin()
                .join(event.bonusRewards, bonusReward).fetchJoin()
                .where(event.id.eq(eventId))
                .distinct()
                .fetchOne();
    }
}
