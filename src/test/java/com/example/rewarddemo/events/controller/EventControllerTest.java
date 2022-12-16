package com.example.rewarddemo.events.controller;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.repository.EventRepository;
import com.example.rewarddemo.events.reward.entity.BonusReward;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {
    private static final String EVENT_API_PATH = "/v1/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("이벤트 정보 조회 api test")
    public void queryEventTest() throws Exception {
        // given
        Event event = event();

        // when & then
        mockMvc.perform(get(EVENT_API_PATH + "/{eventId}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("eventId").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("reward").exists())
                .andExpect(jsonPath("reward.no").exists())
                .andExpect(jsonPath("reward.amount").exists())
                .andExpect(jsonPath("bonusRewards").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.participate-event").exists())
        ;
    }

    @Test
    @DisplayName("이벤트 정보 조회 api test - 없는 event 정보인 경우")
    public void queryEventTest_notFound() throws Exception {
        // when & then
        mockMvc.perform(get(EVENT_API_PATH + "/{eventId}", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("이벤트 정보 조회 api test")
    public void queryEventsTest() throws Exception {
        // given
        event();
        event();

        // when & then
        mockMvc.perform(get(EVENT_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_embedded.events[0].eventId").exists())
                .andExpect(jsonPath("_embedded.events[0].title").exists())
                .andExpect(jsonPath("_embedded.events[0].description").exists())
                .andExpect(jsonPath("_embedded.events[0].reward").exists())
                .andExpect(jsonPath("_embedded.events[0].reward.no").exists())
                .andExpect(jsonPath("_embedded.events[0].reward.amount").exists())
                .andExpect(jsonPath("_embedded.events[0].bonusRewards").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    private Event event() {
        int randomInt = new Random().nextInt();
        String eventId = "testReward" + randomInt;
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
        return eventRepository.save(event);
    }
}