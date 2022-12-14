package com.example.rewarddemo.events.reward.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import com.example.rewarddemo.events.entity.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BonusReward extends BaseTimeEntity {
    private static final int TEN_DAYS = 10;

    @Id
    @GeneratedValue
    private Long no;

    private Long amount;

    private Long standardDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_no")
    private Event event;

    public BonusReward(Long amount, Long standardDays, Event event) {
        this.amount = amount;
        this.standardDays = standardDays;
        this.event = event;
    }

    /**
     * 이벤트 세팅
     * 연관관계 편의 method
     *
     * @param event
     */
    public void setEvent(Event event) {
        if (this.event != null) {
            this.event.getBonusRewards().remove(this);
        }
        this.event = event;
        event.getBonusRewards().add(this);
    }

    /**
     * 추가 보상 대상 여부 체크
     *
     * @param continuousDays 이벤트 연속 참여 일수
     * @return 추가 보상 대상 여부
     */
    public boolean isRewardTarget(long continuousDays) {
        long ZERO = 0L;
        if (continuousDays == ZERO) {
            return false;
        }
        long standardDays = continuousDays % TEN_DAYS == ZERO ? TEN_DAYS : continuousDays % TEN_DAYS;
        return this.standardDays == standardDays;
    }
}
