package com.example.rewarddemo.events.reward.entity;

import com.example.rewarddemo.events.entity.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BonusRewardTest {

    @Test
    @DisplayName("추가 보상 객체 생성 test")
    public void javaBean() throws Exception {
        // given
        Event event = new Event("id", "title", "description");
        long amount = 300L;
        long standardDays = 3L;

        // when
        BonusReward bonusReward = new BonusReward(amount, standardDays, event);

        // then
        assertThat(bonusReward).isNotNull();
        assertThat(bonusReward.getAmount()).isEqualTo(amount);
        assertThat(bonusReward.getStandardDays()).isEqualTo(standardDays);
        assertThat(bonusReward.getEvent()).usingRecursiveComparison().isEqualTo(event);
    }
}