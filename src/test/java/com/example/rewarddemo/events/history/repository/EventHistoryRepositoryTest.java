package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.adapter.querydsl.config.QuerydslConfiguration;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.entity.EventHistory;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.util.TestDataInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class EventHistoryRepositoryTest extends TestDataInitializer {
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
        LocalDate participateDate = LocalDate.now();
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participateDate);

        // when
        EventHistory savedHistory = participationRepository.save(history);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getNo()).isNotNull();
        assertThat(savedHistory.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedHistory.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedHistory.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(savedHistory.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(savedHistory.getParticipateDate()).isEqualTo(participateDate);
        assertThat(savedHistory.getCreatedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
        assertThat(savedHistory.getModifiedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
    }

    @Test
    @DisplayName("참여 이력 저장 test - 유니크 제약조건 확인")
    public void saveUniqueTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        long rewardAmount = 100L;
        long continuousDays = 2L;
        LocalDate participateDate = LocalDate.now();
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participateDate);

        // given - save history
        participationRepository.save(history);

        EventHistory sameHistory = new EventHistory(member, event, rewardAmount, continuousDays, participateDate);

        // when
        assertThatThrownBy(() -> participationRepository.saveAndFlush(sameHistory))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}