package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"member_no", "participate_date", "event_id"})}
)
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

    @Column(name = "participate_date")
    private LocalDate participateDate;

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
        this.participateDate = participatedAt.toLocalDate();
    }
}
