package com.example.rewarddemo.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 응답 에러 객체
 */
@RequiredArgsConstructor
@Getter
public class Error {
    private final HttpStatus status;
    private final String message;
}
