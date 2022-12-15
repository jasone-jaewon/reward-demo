package com.example.rewarddemo.events.exception;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class ClosedEventException extends RuntimeException {

    public ClosedEventException(String eventId, LocalDateTime participatedAt) {
        super(MessageFormat.format("이벤트 마감되었습니다. eventId: {0}, participatedAt: {1}", eventId, participatedAt));
    }
}
