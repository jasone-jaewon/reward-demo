package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.AlreadyParticipateEventException;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventHistoryTest {

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 이벤트 처음 참여 이력")
    public void createFirstParticipateHistory() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();

        // when
        EventHistory history = EventHistory.createFirstParticipateHistory(member, event, participatedAt);

        // then
        assertThat(history).isNotNull();
        assertThat(history.getMember()).isEqualTo(member);
        assertThat(history.getEvent()).isEqualTo(event);
        assertThat(history.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(history.getContinuousDays()).isEqualTo(1L);
        assertThat(history.getParticipatedAt()).isEqualTo(participatedAt);
        assertThat(history.getParticipateDate()).isEqualTo(participatedAt.toLocalDate());
    }

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 지난 이력 없는 경우")
    public void createFirstParticipateHistoryTest() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();

        // when
        EventHistory history = EventHistory.createEventHistory(member, event, null, participatedAt);

        // then
        assertThat(history).isNotNull();
        assertThat(history.getMember()).isEqualTo(member);
        assertThat(history.getEvent()).isEqualTo(event);
        assertThat(history.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(history.getParticipatedAt()).isEqualTo(participatedAt);
        assertThat(history.getParticipateDate()).isEqualTo(participatedAt.toLocalDate());
        assertThat(history.getContinuousDays()).isEqualTo(1L);
    }

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 당일 이미 참여한 경우")
    public void createFirstParticipateHistoryTest_alreadyParticipated() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();

        EventHistory latestHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt);

        // when
        assertThatThrownBy(() -> EventHistory.createEventHistory(member, event, latestHistory, participatedAt))
                .isInstanceOf(AlreadyParticipateEventException.class);
    }

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 지난 이력이 어제인 경우")
    public void createFirstParticipateHistoryTest_participatedInYesterday() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();

        EventHistory latestHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt.minusDays(1));

        // when
        EventHistory history = EventHistory.createEventHistory(member, event, latestHistory, participatedAt);

        // then
        assertThat(history).isNotNull();
        assertThat(history.getContinuousDays()).isEqualTo(latestHistory.getContinuousDays() + 1L);
    }

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test - 지난 이력이 어제가 아닌 경우")
    public void createFirstParticipateHistoryTest_participatedInFewDaysAgo() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        LocalDateTime participatedAt = LocalDateTime.now();

        EventHistory latestHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt.minusDays(3));

        // when
        EventHistory history = EventHistory.createEventHistory(member, event, latestHistory, participatedAt);

        // then
        assertThat(history).isNotNull();
        assertThat(history.getContinuousDays()).isEqualTo(1L);
    }
}