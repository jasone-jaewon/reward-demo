package com.example.rewarddemo.events.reward.entity;

import com.example.rewarddemo.events.entity.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideLongAndExpected")
    @DisplayName("보상 대상 여부 조회 test")
    public void isRewardTargetTest(long standardDays, long continuousDays, boolean expected) throws Exception {
        // given
        Event event = new Event("id", "title", "description");
        long amount = 300L;
        BonusReward bonusReward = new BonusReward(amount, standardDays, event);

        // when
        boolean isTarget = bonusReward.isRewardTarget(continuousDays);

        // then
        assertThat(isTarget).isEqualTo(expected);
    }

    private static Stream<Arguments> provideLongAndExpected() {
        return Stream.of(
                Arguments.of(3L, 0L, false),
                Arguments.of(3L, 2L, false),
                Arguments.of(3L, 3L, true),
                Arguments.of(3L, 13L, true),

                Arguments.of(5L, 5L, true),
                Arguments.of(5L, 14L, false),
                Arguments.of(5L, 15L, true),

                Arguments.of(10L, 10L, true),
                Arguments.of(10L, 18L, false),
                Arguments.of(10L, 20L, true)
        );
    }
}