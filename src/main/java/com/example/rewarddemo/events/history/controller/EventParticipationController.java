package com.example.rewarddemo.events.history.controller;

import com.example.rewarddemo.adapter.hateoas.link.LinkGenerator;
import com.example.rewarddemo.events.history.controller.dto.EventParticipationRequest.ParticipateRequest;
import com.example.rewarddemo.events.history.controller.dto.EventParticipationResponse;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.events.history.service.EventParticipationService;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/events/{eventId}/participations", produces = MediaTypes.HAL_JSON_VALUE)
public class EventParticipationController {
    private final EventParticipationService participationService;
    private final MemberService memberService;

    /**
     * 이벤트 참여 이력 조회
     * @param eventId 이벤트 id
     * @param participateDate 조회 날짜
     * @param pageable page 설정 정보
     * @param assembler page assembler
     * @return 날짜에 해당하는 이벤트 참여 이력 목록
     */
    @GetMapping
    public PagedModel<EventParticipationResponse> queryParticipations(
            @PathVariable String eventId,
            @RequestParam LocalDate participateDate,
            Pageable pageable,
            PagedResourcesAssembler<EventParticipation> assembler
    ) {
        Page<EventParticipation> participations = participationService.findByParticipateDate(eventId, participateDate, pageable);
        var response = assembler.toModel(participations, EventParticipationResponse::of);
        Link selfLink = linkTo(
                methodOn(EventParticipationController.class)
                        .queryParticipations(eventId, participateDate, pageable, assembler)
        ).withSelfRel();
        Link profileLink = LinkGenerator.profileLink(EventParticipationController.class);
        response.add(selfLink, profileLink);
        return response;
    }

    /**
     * 이벤트 참여 이력 조회
     * @param eventId 이벤트 id
     * @param participationNo 참여이력 번호
     * @return 번호에 해당하는 참여이력
     */
    @GetMapping("/{participationNo}")
    public EventParticipationResponse queryParticipation(
            @PathVariable String eventId,
            @PathVariable long participationNo
    ) {
        EventParticipation participation = participationService.findByNo(participationNo);
        var response = EventParticipationResponse.of(participation);
        Link profileLink = LinkGenerator.profileLink(EventParticipationController.class);
        response.add(profileLink);
        return response;
    }

    /**
     * 이벤트 참여
     * @param eventId 이벤트 id
     * @param request 참여 요청
     * @return 이벤트 참여 이력 정보
     */
    @PostMapping
    public EventParticipationResponse participate(
            @PathVariable String eventId,
            @RequestBody ParticipateRequest request
    ) {
        Member member = memberService.findByNo(request.memberNo());
        long participationNo = participationService.participate(eventId, member);
        EventParticipation participation = participationService.findByNo(participationNo);
        var response = EventParticipationResponse.of(participation);
        Link profileLink = LinkGenerator.profileLink(EventParticipationController.class);
        response.add(profileLink);
        return response;
    }
}
