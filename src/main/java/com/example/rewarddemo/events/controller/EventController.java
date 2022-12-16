package com.example.rewarddemo.events.controller;

import com.example.rewarddemo.adapter.hateoas.link.LinkGenerator;
import com.example.rewarddemo.events.controller.dto.EventResponse;
import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.controller.EventParticipationController;
import com.example.rewarddemo.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventService eventService;

    /**
     * 이벤트 정보 목록 조회
     *
     * @return 이벤트 정보 목록
     */
    @GetMapping
    public CollectionModel<EventResponse> queryEvents() {
        List<Event> events = eventService.findAll();
        List<EventResponse> eventResponses = EventResponse.of(events);
        Link selfLink = linkTo(methodOn(EventController.class).queryEvents()).withSelfRel();
        Link profileLink = LinkGenerator.profileLink(EventController.class);
        var response = CollectionModel.of(eventResponses);
        response.add(selfLink, profileLink);
        return response;
    }

    /**
     * 이벤트 id 에 해당하는 이벤트 조회
     *
     * @param eventId 이벤트 id
     * @return 이벤트
     */
    @GetMapping("/{eventId}")
    public EntityModel<EventResponse> queryEvent(@PathVariable String eventId) {
        Event event = eventService.findEventById(eventId);
        EventResponse eventResponse = EventResponse.of(event);
        Link selfLink = linkTo(EventController.class).slash(event.getId()).withSelfRel();
        Link participationLink = linkTo(methodOn(EventParticipationController.class).participate(event.getId(), null))
                .withRel("participate-event");
        Link profileLink = LinkGenerator.profileLink(EventController.class);
        EntityModel<EventResponse> response = EntityModel.of(eventResponse);
        response.add(profileLink, selfLink, participationLink);
        return response;
    }
}
