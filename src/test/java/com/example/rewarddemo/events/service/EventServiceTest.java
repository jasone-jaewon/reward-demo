package com.example.rewarddemo.events.service;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.EventNotFoundException;
import com.example.rewarddemo.events.repository.EventRepository;
import com.example.rewarddemo.util.TestDataInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Test
    @DisplayName("event id에 해당하는 이벤트 조회 test - event 있는 경우")
    public void findByEventIdTest() throws Exception {
        // given
        Event expected = TestDataInitializer.TEST_EVENT;
        String eventId = expected.getId();
        when(eventRepository.findEventWithRewardById(eventId)).thenReturn(Optional.of(expected));

        // when
        Event event = eventService.findEventById(eventId);

        // then
        assertThat(event).isEqualTo(expected);
    }

    @Test
    @DisplayName("event id에 해당하는 이벤트 조회 test - event 없는 경우")
    public void findByEventIdTest_notExist() throws Exception {
        // given
        String eventId = "testId1";
        when(eventRepository.findEventWithRewardById(eventId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> eventService.findEventById(eventId))
                .isInstanceOf(EventNotFoundException.class);
    }

}