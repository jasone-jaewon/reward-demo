package com.example.rewarddemo.member.controller;

import com.example.rewarddemo.adapter.hateoas.link.LinkGenerator;
import com.example.rewarddemo.error.exception.BadRequestException;
import com.example.rewarddemo.member.controller.dto.MemberRequest.MemberJoinRequest;
import com.example.rewarddemo.member.controller.dto.MemberRequest.MemberLoginRequest;
import com.example.rewarddemo.member.controller.dto.MemberResponse;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원 가입
     *
     * @param joinRequest 가입 요청
     * @return 가입 회원 정보
     */
    @PostMapping("/join")
    public ResponseEntity<MemberResponse> join(@RequestBody @Valid MemberJoinRequest joinRequest, Errors errors) {
        checkErrors(errors);

        long memberNo = memberService.join(joinRequest.toEntity());
        Member member = memberService.findByNo(memberNo);
        var memberResponse = MemberResponse.of(member);

        LinkBuilder selfLinkBuilder = linkTo(methodOn(MemberController.class).join(joinRequest, errors));
        Link selfLink = selfLinkBuilder.withSelfRel();
        Link profileLink = LinkGenerator.profileLink(MemberController.class);
        Link queryLink = linkTo(methodOn(MemberController.class).queryMember(memberNo)).withRel("query-member");
        memberResponse.add(selfLink, profileLink, queryLink);
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(memberResponse);
    }

    /**
     * 로그인
     *
     * @param loginRequest id, password 요청
     * @return 로그인된 유저 정보
     */
    @PostMapping("/login")
    public EntityModel<MemberResponse> login(@RequestBody MemberLoginRequest loginRequest, Errors errors) {
        checkErrors(errors);

        Member loggedInMember = memberService.login(loginRequest.memberId(), loginRequest.password());
        var memberResponse = MemberResponse.of(loggedInMember);
        Link selfLink = linkTo(methodOn(MemberController.class).login(loginRequest, errors)).withSelfRel();
        Link profileLink = LinkGenerator.profileLink(MemberController.class);
        Link queryLink = linkTo(methodOn(MemberController.class).queryMember(loggedInMember.getNo())).withRel("query-member");
        memberResponse.add(selfLink, profileLink, queryLink);
        return EntityModel.of(memberResponse);
    }

    /**
     * 회원 정보 조회
     *
     * @param memberNo 회원번호
     * @return 회원번호에 해당하는 회원정보
     */
    @GetMapping("/{memberNo}")
    public EntityModel<MemberResponse> queryMember(@PathVariable Long memberNo) {
        Member foundMember = memberService.findByNo(memberNo);
        var memberResponse = MemberResponse.of(foundMember);
        Link selfLink = linkTo(methodOn(MemberController.class).queryMember(memberNo)).withSelfRel();
        Link profileLink = LinkGenerator.profileLink(MemberController.class);
        memberResponse.add(selfLink, profileLink);
        return EntityModel.of(memberResponse);
    }

    private static void checkErrors(Errors errors) {
        if (errors.hasFieldErrors()) {
            FieldError fieldError = errors.getFieldError();
            assert fieldError != null;
            throw new BadRequestException(fieldError.getDefaultMessage());
        }
    }
}
