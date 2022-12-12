package com.example.rewarddemo.member.repository;

import com.example.rewarddemo.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 정보 저장 test")
    public void saveTest() throws Exception {
        // given
        String memberId = "testId";
        String password = "testPassword";
        Member member = new Member(memberId, password);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getNo()).isNotNull();
        assertThat(savedMember).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    @DisplayName("회원 정보 저장 test - id null")
    public void saveNullableIdTest() throws Exception {
        // given
        Member member = new Member(null, "testPassword");

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회원 정보 저장 test - id unique")
    public void saveUniqueIdTest() throws Exception {
        // given
        String memberId = "testId";
        String password = "testPassword";
        Member member = new Member(memberId, password);
        memberRepository.save(member);

        String otherPassword = "testPassword1";
        Member otherMember = new Member(memberId, otherPassword);

        // when & then
        assertThatThrownBy(() -> memberRepository.saveAndFlush(otherMember))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회원 정보 저장 test - password null")
    public void saveNullablePasswordTest() throws Exception {
        // given
        Member member = new Member("testId", null);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회원 id로 회원 조회 test")
    public void findByMemberIdTest() throws Exception {
        // given
        String memberId = "testId";
        String password = "testPassword";
        Member member = new Member(memberId, password);
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByMemberId(memberId);

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).usingRecursiveComparison().isEqualTo(member);
    }
}