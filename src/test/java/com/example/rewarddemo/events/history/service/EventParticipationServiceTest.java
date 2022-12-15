package com.example.rewarddemo.events.history.service;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.AlreadyParticipateEventException;
import com.example.rewarddemo.events.exception.ClosedEventException;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.events.history.repository.EventParticipationRepository;
import com.example.rewarddemo.events.reward.entity.BonusReward;
import com.example.rewarddemo.events.service.EventService;
import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private static final long MAXIMUM_COUNT = 10L;

    @InjectMocks
    private EventParticipationService participationService;
    @Mock
    private EventParticipationRepository participationRepository;
    @Mock
    private EventService eventService;

    @Test
    @DisplayName("이벤트 참여 test - 마감된 이벤트인 경우")
    public void participateTest_closed_event() throws Exception {
        // given
        Event testEvent = Event.rewardEvent("reward", "title", "description", 200L);
        String eventId = testEvent.getId();
        Member testMember = new Member(2L, "memberId", "password", 100L);

        when(eventService.findEventById(eventId)).thenReturn(testEvent);
        when(participationRepository.countByEventIdAndParticipateDate(eventId, LocalDate.now()))
                .thenReturn(MAXIMUM_COUNT);
        // when & then
        assertThatThrownBy(() -> participationService.participate(eventId, testMember))
                .isInstanceOf(ClosedEventException.class);
    }

    @Test
    @DisplayName("이벤트 참여 test - 당일 이미 참여한 이벤트인 경우")
    public void participateTest_alreadyParticipate() throws Exception {
        // given
        Event testEvent = Event.rewardEvent("reward", "title", "description", 200L);
        String eventId = testEvent.getId();
        Member testMember = new Member(2L, "memberId", "password", 100L);
        LocalDateTime participatedAt = LocalDateTime.now();

        when(eventService.findEventById(eventId)).thenReturn(testEvent);
        when(participationRepository.countByEventIdAndParticipateDate(eventId, participatedAt.toLocalDate()))
                .thenReturn(MAXIMUM_COUNT - 1L);


        long continuousDays = 10L;
        long totalRewardAmount = testEvent.getTotalRewardAmount(continuousDays);
        EventParticipation participation = EventParticipation.createParticipation(
                testMember,
                testEvent,
                continuousDays,
                totalRewardAmount,
                participatedAt
        );

        when(participationRepository.findLatestParticipation(eventId, testMember.getNo()))
                .thenReturn(Optional.of(participation));

        // when & then
        assertThatThrownBy(() -> participationService.participate(eventId, testMember))
                .isInstanceOf(AlreadyParticipateEventException.class);
    }

    @Test
    @DisplayName("이벤트 참여 test - 연속 참여인 경우")
    public void participateTest_continuousParticipation() throws Exception {
        // given
        long continuousDays = 10L;
        long rewardAmount = 200L;
        long expectedBonusAmount = 1000L;
        long expectedRewardAmount = expectedBonusAmount + rewardAmount;
        long beforeMemberPoint = 100L;
        long expectedMemberPoint = expectedRewardAmount + beforeMemberPoint;

        Event testEvent = Event.rewardEvent("reward", "title", "description", rewardAmount);
        BonusReward bonusReward1 = new BonusReward(300L, 3L, testEvent);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, testEvent);
        BonusReward bonusReward3 = new BonusReward(expectedBonusAmount, continuousDays, testEvent);
        testEvent.addBonusRewards(List.of(bonusReward1, bonusReward2, bonusReward3));

        String eventId = testEvent.getId();
        Member testMember = new Member(2L, "memberId", "password", beforeMemberPoint);
        LocalDateTime participatedAt = LocalDateTime.now();

        when(eventService.findEventById(eventId)).thenReturn(testEvent);
        when(participationRepository.countByEventIdAndParticipateDate(eventId, participatedAt.toLocalDate()))
                .thenReturn(MAXIMUM_COUNT - 1L);

        long totalRewardAmount = testEvent.getTotalRewardAmount(continuousDays);
        EventParticipation latestParticipation = EventParticipation.createParticipation(
                testMember,
                testEvent,
                continuousDays - 1L,
                rewardAmount,
                participatedAt.minusDays(1)
        );

        when(participationRepository.findLatestParticipation(eventId, testMember.getNo()))
                .thenReturn(Optional.of(latestParticipation));

        EventParticipation expected = new EventParticipation(
                2L,
                testMember,
                testEvent,
                totalRewardAmount,
                continuousDays,
                participatedAt,
                participatedAt.toLocalDate()
        );
        when(participationRepository.save(any())).thenReturn(expected);

        // when
        participationService.participate(eventId, testMember);

        // then
        assertThat(testMember.getPoint()).isEqualTo(expectedMemberPoint);
    }

    @Test
    @DisplayName("이벤트 참여 test")
    public void participateTest() throws Exception {
        // given
        long continuousDays = 10L;
        long rewardAmount = 200L;
        long beforeMemberPoint = 100L;
        long expectedMemberPoint = rewardAmount + beforeMemberPoint;

        Event testEvent = Event.rewardEvent("reward", "title", "description", rewardAmount);
        BonusReward bonusReward1 = new BonusReward(300L, 3L, testEvent);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, testEvent);
        BonusReward bonusReward3 = new BonusReward(1000L, 10L, testEvent);
        testEvent.addBonusRewards(List.of(bonusReward1, bonusReward2, bonusReward3));

        String eventId = testEvent.getId();
        Member testMember = new Member(2L, "memberId", "password", beforeMemberPoint);
        LocalDateTime participatedAt = LocalDateTime.now();

        when(eventService.findEventById(eventId)).thenReturn(testEvent);
        when(participationRepository.countByEventIdAndParticipateDate(eventId, participatedAt.toLocalDate()))
                .thenReturn(MAXIMUM_COUNT - 1L);

        EventParticipation latestParticipation = EventParticipation.createParticipation(
                testMember,
                testEvent,
                continuousDays - 1L,
                rewardAmount,
                participatedAt.minusDays(2)
        );

        when(participationRepository.findLatestParticipation(eventId, testMember.getNo()))
                .thenReturn(Optional.of(latestParticipation));

        EventParticipation expected = new EventParticipation(
                2L,
                testMember,
                testEvent,
                rewardAmount,
                1L,
                participatedAt,
                participatedAt.toLocalDate()
        );
        when(participationRepository.save(any())).thenReturn(expected);

        // when
        participationService.participate(eventId, testMember);

        // then
        assertThat(testMember.getPoint()).isEqualTo(expectedMemberPoint);
    }
}