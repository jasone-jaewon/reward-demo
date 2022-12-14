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
        LocalDateTime participatedAt = LocalDateTime.now();
        EventHistory history = EventHistory.createFirstParticipateHistory(member, event, participatedAt);

        // when
        EventHistory savedHistory = historyRepository.save(history);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getNo()).isNotNull();
        assertThat(savedHistory.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedHistory.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedHistory.getContinuousDays()).isEqualTo(1L);
        assertThat(savedHistory.getRewardAmount()).isEqualTo(event.getTotalRewardAmount(1L));
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
        LocalDateTime participatedAt = LocalDateTime.now();
        EventHistory history = EventHistory.createFirstParticipateHistory(member, event, participatedAt);

        // given - save history
        historyRepository.save(history);

        EventHistory sameHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt);

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

        EventHistory expectedHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt);
        historyRepository.save(expectedHistory);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventHistory otherMemberHistory = EventHistory.createFirstParticipateHistory(otherMember, event, participatedAt);
        historyRepository.save(otherMemberHistory);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        EventHistory otherEventHistory = EventHistory.createFirstParticipateHistory(member, otherEvent, participatedAt);
        historyRepository.save(otherEventHistory);

        EventHistory otherDateHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAt.plusDays(1));
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

        EventHistory firstHistory = EventHistory.createFirstParticipateHistory(member, event, participatedAtFirst);
        historyRepository.save(firstHistory);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventHistory secondHistory = EventHistory.createFirstParticipateHistory(otherMember, event, participatedAtSecond);
        historyRepository.save(secondHistory);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        EventHistory otherEventHistory = EventHistory.createFirstParticipateHistory(member, otherEvent, participatedAtFirst);
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

    @Test
    @DisplayName("가장 최근 이벤트 참여이력 조회 test")
    public void findLatestHistoryTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAt = LocalDateTime.of(participateDate, LocalTime.MIN);

        historyRepository.save(EventHistory.createFirstParticipateHistory(member, event, participatedAt.minusDays(2)));
        historyRepository.save(EventHistory.createFirstParticipateHistory(member, event, participatedAt.minusDays(1)));
        EventHistory expected = historyRepository.save(EventHistory.createFirstParticipateHistory(member, event, participatedAt));

        // when
        Optional<EventHistory> latestHistory = historyRepository.findLatestHistory(event.getId(), member.getNo());

        // then
        assertThat(latestHistory).isPresent();
        assertThat(latestHistory.get()).isEqualTo(expected);
    }
}