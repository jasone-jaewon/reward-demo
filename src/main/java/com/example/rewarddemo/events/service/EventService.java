package com.example.rewarddemo.events.service;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.exception.EventNotFoundException;
import com.example.rewarddemo.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    /**
     * 이벤트 정보 조회
     *
     * @param eventId 이벤트 id
     * @return 이벤트 정보(보상 제외)
     */
    public Event findEventById(String eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        return event.get();
    }
}
