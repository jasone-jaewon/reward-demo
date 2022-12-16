package com.example.rewarddemo.events.controller.dto;

import com.example.rewarddemo.events.reward.entity.BonusReward;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "이벤트 추가 보상")
@AllArgsConstructor
@Getter
public class BonusRewardResponse extends RepresentationModel<BonusRewardResponse> {
    @Schema(description = "추가 보상 no")
    private long no;

    @Schema(description = "지급량")
    private long amount;

    @Schema(description = "보상 지급 기준 일")
    private long standardDays;

    public static BonusRewardResponse of(BonusReward bonusReward) {
        if (bonusReward == null) {
            return null;
        }
        return new BonusRewardResponse(
                bonusReward.getNo(),
                bonusReward.getAmount(),
                bonusReward.getStandardDays()
        );
    }
}
