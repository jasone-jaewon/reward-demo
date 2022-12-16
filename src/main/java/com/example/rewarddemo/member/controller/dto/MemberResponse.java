package com.example.rewarddemo.member.controller.dto;

import com.example.rewarddemo.member.controller.MemberController;
import com.example.rewarddemo.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Schema(description = "회원 Response")
@Relation(collectionRelation = "members", itemRelation = "member")
@Getter
public class MemberResponse extends RepresentationModel<MemberResponse> {
    @Schema(description = "회원 no")
    private long memberNo;

    @Schema(description = "회원 id")
    private String memberId;

    @Schema(description = "보유 포인트")
    private long point;

    public MemberResponse(Link selfLink, long memberNo, String memberId, long point) {
        super(selfLink);
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.point = point;
    }

    public static MemberResponse of(Member member) {
        Link selfLink = linkTo(MemberController.class).slash(member.getNo()).withSelfRel();
        return new MemberResponse(selfLink, member.getNo(), member.getMemberId(), member.getPoint());
    }
}
