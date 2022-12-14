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
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class EventHistoryRepositoryTest extends TestDataInitializer {
    @Autowired
    private EventHistoryRepository historyRepository;

    @Test
    @DisplayName("참여 이력 저장 test")
    public void saveTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        long rewardAmount = 100L;
        long continuousDays = 2L;
        LocalDateTime participatedAt = LocalDateTime.now();
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);

        // when
        EventHistory savedHistory = historyRepository.save(history);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getNo()).isNotNull();
        assertThat(savedHistory.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedHistory.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedHistory.getRewardAmount()).isEqualTo(rewardAmount);
        assertThat(savedHistory.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(savedHistory.getParticipatedAt()).isEqualTo(participatedAt);
        assertThat(savedHistory.getParticipateDate()).isEqualTo(participatedAt.toLocalDate());
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
        LocalDateTime participatedAt = LocalDateTime.now();
        EventHistory history = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);

        // given - save history
        historyRepository.save(history);

        EventHistory sameHistory = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);

        // when
        assertThatThrownBy(() -> historyRepository.saveAndFlush(sameHistory))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이벤트 참여 이력 유저별 날짜별 조회 test")
    public void findByMemberNoAndEventIdAndParticipateDateTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();

        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAt = LocalDateTime.of(participateDate, LocalTime.MIN);
        long rewardAmount = 100L;
        long continuousDays = 2L;

        EventHistory expectedHistory = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt);
        historyRepository.save(expectedHistory);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventHistory otherMemberHistory = new EventHistory(otherMember, event, rewardAmount, continuousDays, participatedAt);
        historyRepository.save(otherMemberHistory);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        EventHistory otherEventHistory = new EventHistory(member, otherEvent, rewardAmount, continuousDays, participatedAt);
        historyRepository.save(otherEventHistory);

        EventHistory otherDateHistory = new EventHistory(member, event, rewardAmount, continuousDays, participatedAt.plusDays(1));
        historyRepository.save(otherDateHistory);

        // when
        Optional<EventHistory> historyOptional = historyRepository.findByMemberNoAndEventIdAndParticipateDate(
                member.getNo(),
                event.getId(),
                participateDate
        );

        // then
        assertThat(historyOptional).isNotEmpty();
        EventHistory history = historyOptional.get();
        assertThat(history.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(history.getEvent().getId()).isEqualTo(event.getId());
        assertThat(history.getParticipateDate()).isEqualTo(participateDate);
    }
}