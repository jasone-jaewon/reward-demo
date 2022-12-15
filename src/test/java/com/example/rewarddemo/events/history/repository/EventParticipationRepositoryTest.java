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
    private EventParticipationRepository particiaptionRepository;

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
        EventParticipation history = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt);

        // when
        EventParticipation savedHistory = particiaptionRepository.save(history);

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
        EventParticipation history = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt);

        // given - save history
        particiaptionRepository.save(history);

        EventParticipation sameHistory = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt);

        // when
        assertThatThrownBy(() -> particiaptionRepository.saveAndFlush(sameHistory))
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
        EventParticipation expected = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt);
        particiaptionRepository.save(expected);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();

        EventParticipation otherMemberParticipation = EventParticipation.createParticipation(otherMember, event, continuousDays, totalRewardAmount, participatedAt);
        particiaptionRepository.save(otherMemberParticipation);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        long otherEventRewardAmount = otherEvent.getTotalRewardAmount(continuousDays);
        EventParticipation otherEventParticipation = EventParticipation.createParticipation(member, otherEvent, continuousDays, otherEventRewardAmount, participatedAt);
        particiaptionRepository.save(otherEventParticipation);

        EventParticipation otherDateParticipation = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt.plusDays(1));
        particiaptionRepository.save(otherDateParticipation);

        // when
        Optional<EventParticipation> participationOptional = particiaptionRepository.findByMemberNoAndEventIdAndParticipateDate(
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
    @DisplayName("이벤트 참여 이력 날짜별 조회 test - 페이징")
    public void findAllByEventIdAndParticipateDateTest_Paged() throws Exception {
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
        EventParticipation firstParticipation = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAtFirst);
        particiaptionRepository.save(firstParticipation);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();
        EventParticipation secondParticipation = EventParticipation.createParticipation(otherMember, event, continuousDays, totalRewardAmount, participatedAtSecond);
        particiaptionRepository.save(secondParticipation);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();

        EventParticipation otherEventParticipation = EventParticipation.createParticipation(member, otherEvent, continuousDays, totalRewardAmount, participatedAtFirst);
        particiaptionRepository.save(otherEventParticipation);

        // given - order by participated_at
        PageRequest pageableASC = PageRequest.of(0, 1, Sort.by("participatedAt").ascending());
        PageRequest pageableDESC = PageRequest.of(0, 1, Sort.by("participatedAt").descending());


        // when
        Page<EventParticipation> ascHistories = particiaptionRepository.findAllByEventIdAndParticipateDate(
                event.getId(),
                participateDate,
                pageableASC
        );

        Page<EventParticipation> descHistories = particiaptionRepository.findAllByEventIdAndParticipateDate(
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
    public void findLatestParticipationTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();
        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAt = LocalDateTime.of(participateDate, LocalTime.MIN);
        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);

        particiaptionRepository.save(EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt.minusDays(2)));
        particiaptionRepository.save(EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt.minusDays(1)));
        EventParticipation expected = particiaptionRepository.save(EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt));

        // when
        Optional<EventParticipation> latestParticipation = particiaptionRepository.findLatestParticipation(event.getId(), member.getNo());

        // then
        assertThat(latestParticipation).isPresent();
        assertThat(latestParticipation.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("날짜별 이벤트 참여 이력 갯수 조회 test")
    public void countParticipationTest() throws Exception {
        // given
        Member member = memberRepository.findByMemberId(TEST_MEMBER.getMemberId())
                .orElseThrow();
        Event event = eventRepository.findById(TEST_EVENT.getId())
                .orElseThrow();

        LocalDate participateDate = LocalDate.of(2022, 12, 1);
        LocalDateTime participatedAt = LocalDateTime.of(participateDate, LocalTime.MIN);

        long continuousDays = 3L;
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);
        EventParticipation expected = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt);
        particiaptionRepository.save(expected);

        Member otherMember = memberRepository.findByMemberId(TEST_MEMBER2.getMemberId())
                .orElseThrow();

        EventParticipation otherMemberParticipation = EventParticipation.createParticipation(otherMember, event, continuousDays, totalRewardAmount, participatedAt);
        particiaptionRepository.save(otherMemberParticipation);

        Event otherEvent = eventRepository.findById(TEST_EVENT2.getId())
                .orElseThrow();
        long otherEventRewardAmount = otherEvent.getTotalRewardAmount(continuousDays);
        EventParticipation otherEventParticipation = EventParticipation.createParticipation(member, otherEvent, continuousDays, otherEventRewardAmount, participatedAt);
        particiaptionRepository.save(otherEventParticipation);

        EventParticipation otherDateParticipation = EventParticipation.createParticipation(member, event, continuousDays, totalRewardAmount, participatedAt.plusDays(1));
        particiaptionRepository.save(otherDateParticipation);

        //when
        Long count = particiaptionRepository.countByEventIdAndParticipateDate(event.getId(), participateDate);
        // then
        assertThat(count).isEqualTo(2L);
    }
}