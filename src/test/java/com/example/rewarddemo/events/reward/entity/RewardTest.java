package com.example.rewarddemo.events.reward.entity;

import com.example.rewarddemo.events.entity.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RewardTest {

    @Test
    @DisplayName("Reward 객체 생성 test")
    public void javaBean() {
        // given
        Event event = new Event("id", "title", "description");
        long amount = 100L;

        // when
        Reward reward = new Reward(amount, event);

        // then
        assertThat(reward).isNotNull();
        assertThat(reward.getAmount()).isEqualTo(amount);
        assertThat(reward.getEvent()).isEqualTo(event);
    }
}