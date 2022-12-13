package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventParticipationRepositoryTest {
    @Autowired
    private EventParticipationRepository participationRepository;

    @Test
    @DisplayName("참여 이력 저장 test")
    public void saveTest() throws Exception {
        // given
        Member member = new Member("testId", "testPassword");
        long rewardAmount = 100L;
        Event event = Event.rewardEvent("reward", "title", "description", rewardAmount);
        long continuousDays = 2L;
        LocalDateTime participatedAt = LocalDateTime.now();
        EventParticipation participation = new EventParticipation(member, event, rewardAmount, continuousDays, participatedAt);

        // when
        EventParticipation savedParticipation = participationRepository.save(participation);

        // then
        assertThat(savedParticipation).isNotNull();
        assertThat(savedParticipation.getNo()).isNotNull();
        assertThat(savedParticipation.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedParticipation.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedParticipation.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(savedParticipation.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(savedParticipation.getParticipatedAt()).isEqualTo(participatedAt);
    }
}