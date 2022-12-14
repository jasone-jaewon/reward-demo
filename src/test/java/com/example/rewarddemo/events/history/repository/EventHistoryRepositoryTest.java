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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    @Test
    @DisplayName("이벤트 참여 이력 날짜별 조회 test")
    public void findHistoriesByEventIdAndParticipateDateTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAtFirst = LocalDateTime.of(participateDate, LocalTime.MIN);
        LocalDateTime participatedAtSecond = LocalDateTime.of(participateDate, LocalTime.MAX);
        long rewardAmount = 100L;
        long continuousDays = 2L;

        EventHistory firstHistory = new EventHistory(member, event, rewardAmount, continuousDays, participatedAtFirst);
        historyRepository.save(firstHistory);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventHistory secondHistory = new EventHistory(otherMember, event, rewardAmount, continuousDays, participatedAtSecond);
        historyRepository.save(secondHistory);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        EventHistory otherEventHistory = new EventHistory(member, otherEvent, rewardAmount, continuousDays, participatedAtFirst);
        historyRepository.save(otherEventHistory);

        // given - order by participated_at
        PageRequest pageableASC = PageRequest.of(0, 1, Sort.by("participatedAt").ascending());
        PageRequest pageableDESC = PageRequest.of(0, 1, Sort.by("participatedAt").descending());


        // when
        Page<EventHistory> ascHistories = historyRepository.findHistoriesByEventIdAndParticipateDate(
                event.getId(),
                participateDate,
                pageableASC
        );

        Page<EventHistory> descHistories = historyRepository.findHistoriesByEventIdAndParticipateDate(
                event.getId(),
                participateDate,
                pageableDESC
        );

        // then
        assertThat(ascHistories.getTotalElements()).isEqualTo(2);

        // then - asc
        assertThat(ascHistories.getContent().get(0).getNo()).isEqualTo(firstHistory.getNo());

        // then - desc
        assertThat(descHistories.getContent().get(0).getNo()).isEqualTo(secondHistory.getNo());
    }
}