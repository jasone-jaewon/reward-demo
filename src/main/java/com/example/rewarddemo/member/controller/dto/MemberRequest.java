package com.example.rewarddemo.member.controller.dto;

import com.example.rewarddemo.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberRequest {
    public record MemberJoinRequest(
            @NotBlank(message = "memberId 값이 없습니다.")
            String memberId,
            @NotBlank(message = "password 값이 없습니다.")
            String password
    ) {
        public Member toEntity() {
            return new Member(this.memberId, this.password);
        }
    }

    public record MemberLoginRequest(

            @NotBlank(message = "memberId 값이 없습니다.")
            String memberId,
            @NotBlank(message = "password 값이 없습니다.")
            String password
    ) {
    }
}
