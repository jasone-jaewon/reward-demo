package com.example.rewarddemo.events.reward.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import com.example.rewarddemo.events.entity.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reward extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long no;

    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_no")
    private Event event;

    public Reward(Long amount, Event event) {
        this.amount = amount;
        this.event = event;
    }
}
