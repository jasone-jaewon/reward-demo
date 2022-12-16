package com.example.rewarddemo.member.exception;

import com.example.rewarddemo.error.exception.UnAuthorizedException;

import java.text.MessageFormat;

public class LoginFailException extends UnAuthorizedException {
    public LoginFailException(String id) {
        super(MessageFormat.format("로그읜에 실패하였습니다. id: {0}", id));
    }
}
