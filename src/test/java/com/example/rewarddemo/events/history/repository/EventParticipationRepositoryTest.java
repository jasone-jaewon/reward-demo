package com.example.rewarddemo.events.history.repository;

import com.example.rewarddemo.adapter.querydsl.config.QuerydslConfiguration;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.entity.EventParticipation;
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
class EventParticipationRepositoryTest extends TestDataInitializer {
    @Autowired
    private EventParticipationRepository historyRepository;

    @Test
    @DisplayName("참여 이력 저장 test")
    public void saveTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        LocalDateTime participatedAt = LocalDateTime.now();
        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation history = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt);

        // when
        EventParticipation savedHistory = historyRepository.save(history);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getNo()).isNotNull();
        assertThat(savedHistory.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedHistory.getEvent().getId()).isEqualTo(event.getId());
        assertThat(savedHistory.getContinuousDays()).isEqualTo(continuousDays);
        assertThat(savedHistory.getRewardAmount()).isEqualTo(totalRewardAmount);
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
        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation history = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt);

        // given - save history
        historyRepository.save(history);

        EventParticipation sameHistory = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt);

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

        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation expected = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt);
        historyRepository.save(expected);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();

        EventParticipation otherMemberParticipation = EventParticipation.createEventHistory(otherMember, event, continuousDays, totalRewardAmount, participatedAt);
        historyRepository.save(otherMemberParticipation);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        long otherEventRewardAmount = otherEvent.getTotalRewardAmount(continuousDays);
        EventParticipation otherEventParticipation = EventParticipation.createEventHistory(member, otherEvent, continuousDays, otherEventRewardAmount, participatedAt);
        historyRepository.save(otherEventParticipation);

        EventParticipation otherDateParticipation = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt.plusDays(1));
        historyRepository.save(otherDateParticipation);

        // when
        Optional<EventParticipation> participationOptional = historyRepository.findByMemberNoAndEventIdAndParticipateDate(
                member.getNo(),
                event.getId(),
                participateDate
        );

        // then
        assertThat(participationOptional).isNotEmpty();
        EventParticipation participation = participationOptional.get();
        assertThat(participation.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(participation.getEvent().getId()).isEqualTo(event.getId());
        assertThat(participation.getParticipateDate()).isEqualTo(participateDate);
    }

    @Test
    @DisplayName("이벤트 참여 이력 날짜별 조회 test")
    public void findAllByEventIdAndParticipateDateTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAtFirst = LocalDateTime.of(participateDate, LocalTime.MIN);
        LocalDateTime participatedAtSecond = LocalDateTime.of(participateDate, LocalTime.MAX);


        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation firstParticipation = EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAtFirst);
        historyRepository.save(firstParticipation);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventParticipation secondParticipation = EventParticipation.createEventHistory(otherMember, event, continuousDays, totalRewardAmount, participatedAtSecond);
        historyRepository.save(secondParticipation);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();

        EventParticipation otherEventParticipation = EventParticipation.createEventHistory(member, otherEvent, continuousDays, totalRewardAmount, participatedAtFirst);
        historyRepository.save(otherEventParticipation);

        // given - order by participated_at
        PageRequest pageableASC = PageRequest.of(0, 1, Sort.by("participatedAt").ascending());
        PageRequest pageableDESC = PageRequest.of(0, 1, Sort.by("participatedAt").descending());


        // when
        Page<EventParticipation> ascHistories = historyRepository.findAllByEventIdAndParticipateDate(
                event.getId(),
                participateDate,
                pageableASC
        );

        Page<EventParticipation> descHistories = historyRepository.findAllByEventIdAndParticipateDate(
                event.getId(),
                participateDate,
                pageableDESC
        );

        // then
        assertThat(ascHistories.getTotalElements()).isEqualTo(2);

        // then - asc
        assertThat(ascHistories.getContent().get(0).getNo()).isEqualTo(firstParticipation.getNo());

        // then - desc
        assertThat(descHistories.getContent().get(0).getNo()).isEqualTo(secondParticipation.getNo());
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
        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);

        historyRepository.save(EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt.minusDays(2)));
        historyRepository.save(EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt.minusDays(1)));
        EventParticipation expected = historyRepository.save(EventParticipation.createEventHistory(member, event, continuousDays, totalRewardAmount, participatedAt));

        // when
        Optional<EventParticipation> latestParticipation = historyRepository.findLatestParticipation(event.getId(), member.getNo());

        // then
        assertThat(latestParticipation).isPresent();
        assertThat(latestParticipation.get()).isEqualTo(expected);
    }
}