package com.example.rewarddemo.member.service;

import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.exception.AlreadyExistMemberException;
import com.example.rewarddemo.member.exception.LoginFailException;
import com.example.rewarddemo.member.exception.MemberNotFoundException;
import com.example.rewarddemo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     * @param member 회원 entity
     * @throws IllegalArgumentException 파라미터 회원 정보 없는 경우
     * @throws AlreadyExistMemberException 아이디 이미 존재
     * @return 회원 번호
     */
    @Transactional
    public long join(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        memberRepository.findByMemberId(member.getMemberId())
                .ifPresent(foundMember -> {
                    throw new AlreadyExistMemberException(foundMember.getMemberId());
                });

        Member savedMember = memberRepository.save(member);
        return savedMember.getNo();
    }

    /**
     * 로그인
     * @param id 회원 id
     * @param password 비밀번호
     * @throws MemberNotFoundException 회원 정보 조회 실패
     * @throws LoginFailException 로그인 실패
     * @return 회원 entity
     */
    public Member login(String id, String password) {
        Member member = memberRepository.findByMemberId(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        if (!member.getPassword().equals(password)) {
            throw new LoginFailException(id);
        }
        return member;
    }

    /**
     * 회원 정보 조회
     * @param memberNo 회원 no
     * @throws MemberNotFoundException 회원 정보 조회 실패
     * @return 회원 entity
     */
    public Member findByNo(long memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> new MemberNotFoundException(memberNo));
    }
}
