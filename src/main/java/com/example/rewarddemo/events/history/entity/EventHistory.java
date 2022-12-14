package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class EventHistory extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private Long rewardAmount;

    private Long continuousDays;

    private LocalDateTime participatedAt;

    public EventHistory(
            Member member,
            Event event,
            Long rewardAmount,
            Long continuousDays,
            LocalDateTime participatedAt
    ) {
        this.member = member;
        this.event = event;
        this.rewardAmount = rewardAmount;
        this.continuousDays = continuousDays;
        this.participatedAt = participatedAt;
    }
}
