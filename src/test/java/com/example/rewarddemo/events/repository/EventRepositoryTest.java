package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.adapter.querydsl.config.QuerydslConfiguration;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.reward.entity.BonusReward;
import com.example.rewarddemo.events.reward.entity.Reward;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("이벤트 저장 test")
    public void saveTest() {
        // given
        Event event = eventWithoutReward();

        // when
        Event savedEvent = eventRepository.save(event);

        // then
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getId()).isEqualTo(event.getId());
        assertThat(savedEvent.getTitle()).isEqualTo(event.getTitle());
        assertThat(savedEvent.getDescription()).isEqualTo(event.getDescription());
        assertThat(savedEvent.getReward()).isNull();
        assertThat(savedEvent.getCreatedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
        assertThat(savedEvent.getModifiedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
    }

    @Test
    @DisplayName("보상 이벤트 저장 test")
    public void saveWithReward() throws Exception {
        // given
        Event event = rewardEvent();

        // when
        Event savedEvent = eventRepository.save(event);

        // then
        assertThat(savedEvent).isNotNull();
        Reward reward = savedEvent.getReward();
        assertThat(reward).isNotNull();
        assertThat(reward.getNo()).isNotNull();
        assertThat(reward.getAmount()).isNotNull();
        assertThat(reward.getEvent()).isNotNull();
        assertThat(reward.getCreatedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
        assertThat(reward.getModifiedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
    }

    @Test
    @DisplayName("보상 이벤트 저장 test")
    public void saveWithBonusReward() throws Exception {
        // given
        Event event = rewardEvent();
        BonusReward bonusReward1 = new BonusReward(300L, 3L, event);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, event);
        BonusReward bonusReward3 = new BonusReward(1000L, 10L, event);
        List<BonusReward> bonusRewards = List.of(bonusReward1, bonusReward2, bonusReward3);
        event.addBonusRewards(bonusRewards);

        // when
        Event savedEvent = eventRepository.save(event);

        // then
        assertThat(savedEvent).isNotNull();
        savedEvent.getBonusRewards().forEach(bonusReward -> {
            assertThat(bonusReward).isNotNull();
            assertThat(bonusReward.getNo()).isNotNull();
            assertThat(bonusReward.getAmount()).isIn(300L, 500L, 1000L);
            assertThat(bonusReward.getStandardDays()).isIn(3L, 5L, 10L);
            assertThat(bonusReward.getEvent()).isNotNull();
            assertThat(bonusReward.getCreatedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
            assertThat(bonusReward.getModifiedAt()).isEqualToIgnoringMinutes(LocalDateTime.now());
        });
    }

    @Test
    @DisplayName("이벤트 key 값 조회 test")
    public void findByIdTest() {
        // given
        Event savedEvent = eventRepository.save(eventWithoutReward());

        // when
        Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());

        // then
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get()).usingRecursiveComparison().isEqualTo(savedEvent);
    }

    @Test
    @DisplayName("보상 이벤트 조회 test - fetch 조인 test")
    public void findEventWithRewardByIdTest() throws Exception {
        // given
        Event event = rewardEvent();
        BonusReward bonusReward1 = new BonusReward(300L, 3L, event);
        BonusReward bonusReward2 = new BonusReward(500L, 5L, event);
        BonusReward bonusReward3 = new BonusReward(1000L, 10L, event);
        List<BonusReward> bonusRewards = List.of(bonusReward1, bonusReward2, bonusReward3);
        event.addBonusRewards(bonusRewards);
        eventRepository.saveAndFlush(event);

        // when
        Event rewardEvent = eventRepository.findEventWithRewardById(event.getId());

        // then
        assertThat(rewardEvent).isNotNull();
        assertThat(rewardEvent.getBonusRewards()).isNotEmpty();
        assertThat(rewardEvent.getReward()).isNotNull();
    }

    /**
     * test 보상 없는 event 생성
     *
     * @return event without reward
     */
    private Event eventWithoutReward() {
        String eventId = "reward";
        String title = "매일 00시 00분 00초 선착순 10명 100 포인트 지급!!!";
        String description = """
                • 보상지급방식은사용자가받기를누를때지급하게 됩니다.
                • 선착순 10 명에게 100 포인트의 보상이 지급 되며 10 명 이후에는 지급되지 않아야 합니다.
                • 3일 연속,5일 연속,10일 연속 보상을 받는 경우 300 포인트, 500 포인트, 1,000 포인트를 추가 보상 받게 됩니다.
                • 추가 보상은 10일 까지 이어지며 그 이후 연속 보상 횟수는 1 회 부터 다시 시작 됩니다.""";

        return new Event(eventId, title, description);
    }

    private Event rewardEvent() {
        String eventId = "reward";
        String title = "매일 00시 00분 00초 선착순 10명 100 포인트 지급!!!";
        String description = """
                • 보상지급방식은사용자가받기를누를때지급하게 됩니다.
                • 선착순 10 명에게 100 포인트의 보상이 지급 되며 10 명 이후에는 지급되지 않아야 합니다.
                • 3일 연속,5일 연속,10일 연속 보상을 받는 경우 300 포인트, 500 포인트, 1,000 포인트를 추가 보상 받게 됩니다.
                • 추가 보상은 10일 까지 이어지며 그 이후 연속 보상 횟수는 1 회 부터 다시 시작 됩니다.""";
        long rewardAmount = 100L;

        return Event.rewardEvent(eventId, title, description, rewardAmount);
    }
}