package com.example.rewarddemo.events.controller.dto;

import com.example.rewarddemo.events.controller.EventController;
import com.example.rewarddemo.events.entity.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Schema(description = "이벤트 response")
@Getter
public class EventResponse extends RepresentationModel<EventResponse> {
    @Schema(description = "이벤트 id", example = "reward")
    private final String eventId;

    @Schema(description = "이벤트 제목", example = "매일 00시 1000 포인트 지급")
    private final String title;

    @Schema(description = "이벤트 설명", example = "보상 방식은 사용자가 받기를 누를때 지급.")
    private final String description;

    @Schema(description = "보상 정보")
    private final RewardResponse reward;

    @Schema(description = "추가 보상 정보")
    private final List<BonusRewardResponse> bonusRewards;

    public EventResponse(Link selfLink, String eventId, String title, String description, RewardResponse reward, List<BonusRewardResponse> bonusRewards) {
        super(selfLink);
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.bonusRewards = bonusRewards;
    }

    public static EventResponse of(Event event) {
        Link selfLink = linkTo(EventController.class).slash(event.getId()).withSelfRel();
        List<BonusRewardResponse> bonusRewardResponses = event.getBonusRewards().stream()
                .map(BonusRewardResponse::of)
                .toList();
        return new EventResponse(
                selfLink,
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                RewardResponse.of(event.getReward()),
                bonusRewardResponses
        );
    }

    public static List<EventResponse> of(List<Event> events) {
        return events.stream().map(EventResponse::of).toList();
    }
}
