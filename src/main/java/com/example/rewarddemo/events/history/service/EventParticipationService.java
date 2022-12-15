package com.example.rewarddemo.events.history.service;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.AlreadyParticipateEventException;
import com.example.rewarddemo.events.exception.ClosedEventException;
import com.example.rewarddemo.events.exception.EventNotFoundException;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.events.history.repository.EventParticipationRepository;
import com.example.rewarddemo.events.service.EventService;
import com.example.rewarddemo.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventParticipationService {
    private static final int MAXIMUM_PARTICIPANT_COUNT = 10;
    private final EventParticipationRepository participationRepository;

    private final EventService eventService;

    /**
     * 날짜 별 이벤트 참여이력 조회
     *
     * @param eventId         이벤트 아이디
     * @param participateDate 검색할 참여 날짜
     * @param pageable 페이징
     * @return paged event histories
     */
    public Page<EventParticipation> findByParticipateDate(String eventId, LocalDate participateDate, Pageable pageable) {
        return participationRepository.findAllByEventIdAndParticipateDate(
                eventId,
                participateDate,
                pageable
        );
    }

    /**
     * 이벤트 참여
     * - 참여 인원이 10 명 이상인 경우, 참여 불가
     * - 당일 이벤트 참여시, 중복 참여 불가
     * @param member 회원
     * @param eventId 이벤트 id
     * @throws EventNotFoundException 이벤트 조회 불가
     * @throws ClosedEventException 이벤트 마감으로 인한 참여 불가
     * @throws AlreadyParticipateEventException 이벤트 중복 참여 불가
     * @return 이벤트 참여이력 No
     */
    @Transactional
    public Long participate(String eventId, Member member) {
        LocalDateTime participatedAt = LocalDateTime.now();
        LocalDate participateDate = participatedAt.toLocalDate();
        Event event = eventService.findEventById(eventId);

        long participantCount = participationRepository.countByEventIdAndParticipateDate(eventId, participateDate);
        if (participantCount >= MAXIMUM_PARTICIPANT_COUNT) {
            throw new ClosedEventException(eventId, participatedAt);
        }

        Optional<EventParticipation> latestHistory = participationRepository.findLatestParticipation(eventId, member.getNo());

        // 당일 참여 validate
        if (latestHistory.isPresent()
                && participateDate.isEqual(latestHistory.get().getParticipateDate())
        ) {
            throw new AlreadyParticipateEventException(eventId, member.getMemberId(), participateDate);
        }

        // 연속 참여 일수 설정
        long continuousDays = latestHistory.map(eventHistory -> eventHistory.nextContinuousDays(participateDate))
                .orElse(1L);

        // 지급 포인트 생성
        long totalRewardAmount = event.getTotalRewardAmount(continuousDays);

        // 이벤트 참여
        member.earnPoint(totalRewardAmount);

        // 이벤트 참여 이력 등록
        EventParticipation participation = EventParticipation.createParticipation(
                member,
                event,
                continuousDays,
                totalRewardAmount,
                participatedAt
        );
        return participationRepository.save(participation).getNo();
    }
}
