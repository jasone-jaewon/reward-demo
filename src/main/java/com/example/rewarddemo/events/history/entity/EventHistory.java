package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.AlreadyParticipateEventException;
import com.example.rewarddemo.events.reward.entity.Reward;
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

    private EventHistory(
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
     * 이벤트 첫 참여 이력 생성
     * @param member 회원
     * @param event 이벤트
     * @param participatedAt 이벤트 참여 시각
     * @return 첫 참여 이력
     */
    public static EventHistory createFirstParticipateHistory(Member member, Event event, LocalDateTime participatedAt) {
        Reward reward = event.getReward();
        return new EventHistory(member, event, reward.getAmount(), BASIC_CONTINUOUS_DAY, participatedAt);
    }

    /**
     * 이벤트 참여 이력 생성
     * @param member 회원
     * @param event 이벤트
     * @param latestEventHistory 마지막 이벤트 참여 이력
     * @param participatedAt 이벤트 참여 시각
     * @return 이벤트 참여 이력
     */
    public static EventHistory createEventHistory(Member member, Event event, EventHistory latestEventHistory, LocalDateTime participatedAt) {
        if (latestEventHistory == null) {
            return createFirstParticipateHistory(member, event, participatedAt);
        }

        LocalDate participateDate = participatedAt.toLocalDate();
        LocalDate latestParticipateDate = latestEventHistory.getParticipatedAt().toLocalDate();
        if (participateDate.isEqual(latestParticipateDate)) {
            throw new AlreadyParticipateEventException(event.getId(), member.getMemberId(), participateDate);
        }

        boolean isParticipatedInYesterday = participateDate.minusDays(1).isEqual(latestParticipateDate);
        long continuousDays = isParticipatedInYesterday ? latestEventHistory.getContinuousDays() + BASIC_CONTINUOUS_DAY : BASIC_CONTINUOUS_DAY;

        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);

        return new EventHistory(member, event, totalRewardAmount, continuousDays, participatedAt);
    }
}
