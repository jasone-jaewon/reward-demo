package com.example.rewarddemo.config;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.repository.EventRepository;
import com.example.rewarddemo.events.reward.entity.BonusReward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            EventRepository eventRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                String eventId = "reward";
                String title = "매일 00시 00분 00초 선착순 10명 100 포인트 지급!!!";
                String description = """
                        • 보상지급방식은사용자가받기를누를때지급하게 됩니다.
                        • 선착순 10 명에게 100 포인트의 보상이 지급 되며 10 명 이후에는 지급되지 않아야 합니다.
                        • 3일 연속,5일 연속,10일 연속 보상을 받는 경우 300 포인트, 500 포인트, 1,000 포인트를 추가 보상 받게 됩니다.
                        • 추가 보상은 10일 까지 이어지며 그 이후 연속 보상 횟수는 1 회 부터 다시 시작 됩니다.""";
                Event event = Event.rewardEvent(eventId, title, description, 100L);

                List<BonusReward> bonusRewards = List.of(
                        new BonusReward(300L, 3L, event),
                        new BonusReward(500L, 5L, event),
                        new BonusReward(1000L, 10L, event)
                );

                event.addBonusRewards(bonusRewards);
                eventRepository.save(event);
            }
        };
    }
}
