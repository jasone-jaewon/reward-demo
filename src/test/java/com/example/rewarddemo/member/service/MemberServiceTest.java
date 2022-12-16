package com.example.rewarddemo.member.service;

import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.exception.AlreadyExistMemberException;
import com.example.rewarddemo.member.exception.LoginFailException;
import com.example.rewarddemo.member.exception.MemberNotFoundException;
import com.example.rewarddemo.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 test - 파라미터 회원 Null")
    public void joinTest_parameterNull() throws Exception {
        // when & then
        assertThatThrownBy(() -> memberService.join(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 가입 test - 이미 존재하는 아이디")
    public void joinTest_alreadyExistMember() throws Exception {
        // given
        String memberId = "test1";
        Member member1 = new Member(memberId, "testPassword1");
        Member member2 = new Member(memberId, "testPassword2");

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member1));

        // when & then
        assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(AlreadyExistMemberException.class);
    }

    @Test
    @DisplayName("회원 가입 test")
    public void joinTest() throws Exception {
        // given
        String memberId = "test1";
        String password = "testPassword1";
        Member member = new Member(memberId, password);
        Member expected = new Member(2L, memberId, password, 0L);

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
        when(memberRepository.save(member)).thenReturn(expected);

        // when
        long memberNo = memberService.join(member);

        // then
        assertThat(memberNo).isEqualTo(expected.getNo());
    }

    @Test
    @DisplayName("로그인 Test - 일치하는 회원 정보 없는 경우")
    public void loginTest_notFoundMember() throws Exception {
        // given
        String memberId = "nonexistent";

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.login(memberId, "password"))
                .isInstanceOf(LoginFailException.class);
    }

    @Test
    @DisplayName("로그인 Test - 비밀번호 일치하지 않는 경우")
    public void loginTest_notMatchedPassword() throws Exception {
        // given
        String memberId = "memberId";
        String password = "password";
        String notMatchedPassword = "notMatched";
        Member member = new Member(memberId, password);

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.login(memberId, notMatchedPassword))
                .isInstanceOf(LoginFailException.class);
    }

    @Test
    @DisplayName("로그인 Test")
    public void loginTest() throws Exception {
        // given
        String memberId = "memberId";
        String password = "password";
        Member member = new Member(memberId, password);

        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));

        // when
        Member loggedInMember = memberService.login(memberId, password);

        // then
        assertThat(loggedInMember).isEqualTo(member);
    }

    @Test
    @DisplayName("회원번호로 회원정보 조회 test - 없는 회원 번호인 경우")
    public void findByNoTest_nonexistent() throws Exception {
        // given
        long memberNo = 1L;
        Member member = new Member(memberNo, "test", "password", 0L);
        when(memberRepository.findById(memberNo)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findByNo(memberNo))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("회원번호로 회원정보 조회 test")
    public void findByNoTest() throws Exception {
        // given
        long memberNo = 1L;
        Member member = new Member(memberNo, "test", "password", 0L);
        when(memberRepository.findById(memberNo)).thenReturn(Optional.of(member));

        // when
        Member foundMember = memberService.findByNo(memberNo);

        // then
        assertThat(foundMember).isEqualTo(member);
    }
}