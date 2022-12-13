package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipationTest {

    @Test
    @DisplayName("이벤트 참여 이력 객체 생성 test")
    public void javaBean() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        long continuousDays = 2L;
        LocalDateTime participatedAt = LocalDateTime.now();

        // when
        EventParticipation participation = new EventParticipation(member, event, rewardAmount, continuousDays, participatedAt);

        // then
        assertThat(participation).isNotNull();
        assertThat(participation.getMember()).isEqualTo(member);
        assertThat(participation.getEvent()).isEqualTo(event);
        assertThat(participation.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(participation.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(participation.getParticipatedAt()).isEqualTo(participatedAt);
    }

}