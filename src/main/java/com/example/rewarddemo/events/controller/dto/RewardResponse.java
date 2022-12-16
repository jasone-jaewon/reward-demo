package com.example.rewarddemo.events.controller.dto;

import com.example.rewarddemo.events.reward.entity.Reward;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "이벤트 보상")
@AllArgsConstructor
@Getter
public class RewardResponse extends RepresentationModel<RewardResponse> {
    @Schema(description = "이벤트 보상 no")
    private long no;

    @Schema(description = "보상 지급량")
    private long amount;

    public static RewardResponse of(Reward reward) {
        if (reward == null) {
            return null;
        }
        return new RewardResponse(reward.getNo(), reward.getAmount());
    }
}
