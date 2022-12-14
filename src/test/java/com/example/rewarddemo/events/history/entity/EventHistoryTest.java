package com.example.rewarddemo.events.history.entity;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventHistoryTest {

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
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);

        // then
        assertThat(history).isNotNull();
        assertThat(history.getMember()).isEqualTo(member);
        assertThat(history.getEvent()).isEqualTo(event);
        assertThat(history.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(history.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(history.getParticipatedAt()).isEqualTo(participatedAt);
    }

}