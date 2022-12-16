package com.example.rewarddemo.events.history.controller.dto;

import com.example.rewarddemo.events.controller.dto.EventResponse;
import com.example.rewarddemo.events.history.controller.EventParticipationController;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.member.controller.dto.MemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Schema(description = "이벤트 참여 이력")
@Getter
public class EventParticipationResponse extends RepresentationModel<EventParticipationResponse> {
    @Schema(description = "이벤트 참여 정보 no")
    private final long no;

    @Schema(description = "보상 포인트 지급 량")
    private final long rewardAmount;

    @Schema(description = "연속 참여 일")
    private final long continuousDays;

    @Schema(description = "참여 날짜")
    private final LocalDateTime participatedAt;

    @Schema(description = "참여 이벤트 정보")
    private final EventResponse event;

    @Schema(description = "참여 유저")
    private final MemberResponse member;

    public EventParticipationResponse(Link selfLink, long no, long rewardAmount, long continuousDays, LocalDateTime participatedAt, EventResponse event, MemberResponse member) {
        super(selfLink);
        this.no = no;
        this.rewardAmount = rewardAmount;
        this.continuousDays = continuousDays;
        this.participatedAt = participatedAt;
        this.event = event;
        this.member = member;
    }

    public static EventParticipationResponse of(EventParticipation participation) {
        Link selfLink = linkTo(EventParticipationController.class).slash(participation.getNo()).withSelfRel();
        return new EventParticipationResponse(
                selfLink,
                participation.getNo(),
                participation.getRewardAmount(),
                participation.getContinuousDays(),
                participation.getParticipatedAt(),
                EventResponse.of(participation.getEvent()),
                MemberResponse.of(participation.getMember())
        );
    }
}
