package com.example.rewarddemo.events.exception;

import java.text.MessageFormat;
import java.time.LocalDate;

public class AlreadyParticipateEventException extends RuntimeException {

    public AlreadyParticipateEventException(String eventId, String memberId, LocalDate participateDate) {
        super(MessageFormat.format("이벤트에 이미 참여하였습니다. eventId: {0}, memberId: {1}, participateDate: {2}", eventId, memberId, participateDate));
    }
}
