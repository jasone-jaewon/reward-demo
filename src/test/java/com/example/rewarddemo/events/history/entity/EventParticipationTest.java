package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipationTest {

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 이벤트 처음 참여 이력")
    public void createFirstParticipateHistory() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();
        long continuousDays = 5L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);

        // when
        EventParticipation participation = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt);

        // then
        assertThat(participation).isNotNull();
        assertThat(participation.getMember()).isEqualTo(member);
        assertThat(participation.getEvent()).isEqualTo(event);
        assertThat(participation.getRewardAmount()).isEqualTo(totalRewardAmount);
        assertThat(participation.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(participation.getParticipatedAt()).isEqualTo(participatedAt);
        assertThat(participation.getParticipateDate()).isEqualTo(participatedAt.toLocalDate());
    }

    @ParameterizedTest
    @MethodSource("provideParticipateDateTimeAndExpected")
    @DisplayName("다음 연속 참여일 조회 test")
    public void nextContinuousDaysTest(long continuousDays, LocalDateTime latestParticipatedAt, LocalDateTime participatedAt, long expected) throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation participation = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, latestParticipatedAt);

        // when
        long nextContinuousDays = participation.nextContinuousDays(participatedAt.toLocalDate());

        // then
        assertThat(nextContinuousDays).isEqualTo(expected);
    }

    private static Stream<Arguments> provideParticipateDateTimeAndExpected() {
        long continuousDays = 5L;
        LocalDateTime latestParticipatedAt = LocalDateTime.now();
        return Stream.of(
                Arguments.of(continuousDays, latestParticipatedAt, latestParticipatedAt, continuousDays),
                Arguments.of(continuousDays, latestParticipatedAt, latestParticipatedAt.plusDays(1L), continuousDays + 1L),
                Arguments.of(continuousDays, latestParticipatedAt, latestParticipatedAt.plusDays(2L), 1L),
                Arguments.of(continuousDays, latestParticipatedAt, latestParticipatedAt.minusDays(1L), 1L),
                Arguments.of(continuousDays, latestParticipatedAt, latestParticipatedAt.minusDays(2L), 1L)
        );
    }
}