package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.entity.EventHistory;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventHistoryRepositoryTest {
    @Autowired
    private EventHistoryRepository participationRepository;

    @Test
    @DisplayName("참여 이력 저장 test")
    public void saveTest() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        long continuousDays = 2L;
        LocalDateTime participatedAt = LocalDateTime.now();
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);

        // when
        EventHistory savedHistory = participationRepository.save(history);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getNo()).isNotNull();
        assertThat(savedHistory.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedHistory.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedHistory.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(savedHistory.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(savedHistory.getParticipatedAt()).isEqualTo(participatedAt);
    }
}