package com.example.rewarddemo.member.exception;

import java.text.MessageFormat;

public class AlreadyExistMemberException extends RuntimeException {
    public AlreadyExistMemberException(String id) {
        super(MessageFormat.format("이미 존재하는 아이디입니다. id: {0}", id));
    }
}
