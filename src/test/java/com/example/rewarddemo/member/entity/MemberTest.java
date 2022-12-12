package com.example.rewarddemo.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("회원 객체 생성 test")
    public void javaBean() throws Exception {
        // given
        String memberId = "testId";
        String password = "testPassword";

        // when
        Member member = new Member(memberId, password);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getMemberId()).isEqualTo(memberId);
        assertThat(member.getPassword()).isEqualTo(password);
    }

}