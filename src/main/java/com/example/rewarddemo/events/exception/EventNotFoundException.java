package com.example.rewarddemo.events.exception;

import com.example.rewarddemo.error.exception.NotFoundException;

import java.text.MessageFormat;

public class EventNotFoundException extends NotFoundException {

    public EventNotFoundException(String eventId) {
        super(MessageFormat.format("이벤트 정보를 찾을 수 없습니다. event id: {0}", eventId));
    }
}
