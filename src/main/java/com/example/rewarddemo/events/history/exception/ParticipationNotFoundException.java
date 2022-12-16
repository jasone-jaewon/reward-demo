package com.example.rewarddemo.events.history.exception;

import com.example.rewarddemo.error.exception.NotFoundException;

import java.text.MessageFormat;

public class ParticipationNotFoundException extends NotFoundException {
    public ParticipationNotFoundException(long no) {
        super(MessageFormat.format("참여이력을 찾을 수 없습니다. no: {0}", no));
    }
}
