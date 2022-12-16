package com.example.rewarddemo.member.controller.dto;

import com.example.rewarddemo.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "회원 조회 Response")
@AllArgsConstructor
@Getter
public class MemberResponse extends RepresentationModel<MemberResponse> {
    @Schema(description = "회원 no")
    private long memberNo;

    @Schema(description = "회원 id")
    private String memberId;

    @Schema(description = "보유 포인트")
    private long point;

    public static MemberResponse of(Member member) {
        return new MemberResponse(member.getNo(), member.getMemberId(), member.getPoint());
    }
}
