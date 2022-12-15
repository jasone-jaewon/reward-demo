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
public class EventParticipation extends BaseTimeEntity {
    private static final long BASIC_CONTINUOUS_DAY = 1L;

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

    private EventParticipation(
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

    /**
     * 이벤트 참여 이력 생성
     * @param member 회원
     * @param event 이벤트
     * @param continuousDays 이벤트 연속 참여 일수
     * @param rewardAmount 보상 포인트 양
     * @param participatedAt 이벤트 참여 시각
     * @return 이벤트 참여 이력
     */
    public static EventParticipation createEventHistory(Member member, Event event, long continuousDays, long rewardAmount, LocalDateTime participatedAt) {
        return new EventParticipation(member, event, rewardAmount, continuousDays, participatedAt);
    }
}
