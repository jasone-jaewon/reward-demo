package com.example.rewarddemo.member.exception;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class MemberNotFoundException extends RuntimeException {
    private Long no;
    private String id;

    public MemberNotFoundException(String id) {
        super(MessageFormat.format("회원 정보를 찾을 수 없습니다. id: {0}", id));
        this.id = id;
    }

    public MemberNotFoundException(Long no) {
        super(MessageFormat.format("회원 정보를 찾을 수 없습니다. no: {0}", no));
        this.no = no;
    }
}
