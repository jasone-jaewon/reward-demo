package com.example.rewarddemo.events.entity;

import com.example.rewarddemo.events.reward.entity.BonusReward;
import com.example.rewarddemo.events.reward.entity.Reward;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Reward reward;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonusReward> bonusRewards = new ArrayList<>();

    public Event(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // 보상 이벤트 객체 생성
    public static Event rewardEvent(
            String id,
            String title,
            String description,
            long rewardAmount
    ) {
        Event event = new Event(id, title, description);
        Reward reward = new Reward(rewardAmount, event);
        event.setReward(reward);
        return event;
    }

    /**
     * 보상 세팅 method
     *
     * @param reward
     */
    private void setReward(Reward reward) {
        this.reward = reward;
    }

    /**
     * 추가 보상 세팅 method
     * 연관 관계 편의 메소드
     *
     * @param bonusRewards
     */
    public void addBonusRewards(List<BonusReward> bonusRewards) {
        bonusRewards.forEach(bonusReward -> bonusReward.setEvent(this));
    }
}
