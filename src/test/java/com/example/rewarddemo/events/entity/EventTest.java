package com.example.rewarddemo.events.entity;

import com.example.rewarddemo.events.reward.entity.BonusReward;
import com.example.rewarddemo.events.reward.entity.Reward;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    @DisplayName("event 객체 생성 test")
    public void javaBean() {
        // given
        String eventId = "reward";
        String title = "매일 00시 00분 00초 선착순 10명 100 포인트 지급!!!";
        String description = """
                • 보상지급방식은사용자가받기를누를때지급하게 됩니다.
                • 선착순 10 명에게 100 포인트의 보상이 지급 되며 10 명 이후에는 지급되지 않아야 합니다.
                • 3일 연속,5일 연속,10일 연속 보상을 받는 경우 300 포인트, 500 포인트, 1,000 포인트를 추가 보상 받게 됩니다.
                • 추가 보상은 10일 까지 이어지며 그 이후 연속 보상 횟수는 1 회 부터 다시 시작 됩니다.""";

        // when
        Event event = new Event(eventId, title, description);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(eventId);
        assertThat(event.getTitle()).isEqualTo(title);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("보상 이벤트 객체 생성 test")
    public void rewardEventTest() throws Exception {
        // given
        String eventId = "reward";
        String title = "매일 00시 00분 00초 선착순 10명 100 포인트 지급!!!";
        String description = """
                • 보상지급방식은사용자가받기를누를때지급하게 됩니다.
                • 선착순 10 명에게 100 포인트의 보상이 지급 되며 10 명 이후에는 지급되지 않아야 합니다.
                • 3일 연속,5일 연속,10일 연속 보상을 받는 경우 300 포인트, 500 포인트, 1,000 포인트를 추가 보상 받게 됩니다.
                • 추가 보상은 10일 까지 이어지며 그 이후 연속 보상 횟수는 1 회 부터 다시 시작 됩니다.""";
        long rewardAmount = 100L;

        // when
        Event event = Event.rewardEvent(eventId, title, description, rewardAmount);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(eventId);
        assertThat(event.getTitle()).isEqualTo(title);
        assertThat(event.getDescription()).isEqualTo(description);

        // then - reward
        Reward reward = event.getReward();
        assertThat(reward).isNotNull();
        assertThat(reward.getEvent()).isNotNull();
        assertThat(reward.getAmount()).isEqualTo(rewardAmount);
    }

    @Test
    @DisplayName("추가 보상 연관관계 추가 test")
    public void addBonusRewardsTest() throws Exception {
        // given
        Event event = Event.rewardEvent("id", "test", "description", 100L);
        BonusReward bonusReward1 = new BonusReward(300L, 3L, event);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, event);
        BonusReward bonusReward3 = new BonusReward(1000L, 10L, event);
        List<BonusReward> bonusRewards = List.of(bonusReward1, bonusReward2, bonusReward3);

        // when
        event.addBonusRewards(bonusRewards);

        // then
        assertThat(event.getBonusRewards()).isNotEmpty();
        assertThat(event.getBonusRewards()).contains(bonusReward1, bonusReward2, bonusReward3);
    }

    @Test
    @DisplayName("전체 보상 조회 test")
    public void getTotalRewardAmountTest() throws Exception {
        // given
        long expected = 1100L;
        Event event = Event.rewardEvent("id", "test", "description", 100L);
        BonusReward bonusReward1 = new BonusReward(300L, 3L, event);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, event);
        BonusReward bonusReward3 = new BonusReward(1000L, 10L, event);
        List<BonusReward> bonusRewards = List.of(bonusReward1, bonusReward2, bonusReward3);

        event.addBonusRewards(bonusRewards);

        // when
        long totalRewardAmount = event.getTotalRewardAmount(50L);

        // then
        assertThat(totalRewardAmount).isEqualTo(expected);
    }
}