package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.AlreadyParticipateEventException;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}