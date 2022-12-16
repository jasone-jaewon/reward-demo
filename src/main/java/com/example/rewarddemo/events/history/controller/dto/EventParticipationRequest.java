package com.example.rewarddemo.events.history.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class EventParticipationRequest {
    public record ParticipateRequest(
            @NotBlank(message = "회원 정보는 필수 입니다.")
            long memberNo
    ) {
    }
}
