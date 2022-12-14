package com.example.rewarddemo.util;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.repository.EventRepository;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TestDataInitializer {
    public static final Member TEST_MEMBER = new Member("testId", "testPassword");

    public static final Member TEST_MEMBER2 = new Member("testId2", "testPassword2");
    public static final Event TEST_EVENT = Event.rewardEvent("reward", "title", "description", 100L);
    public static final Event TEST_EVENT2 = Event.rewardEvent("testEvent", "title2", "description2", 300L);

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        // test member 등록
        memberRepository.save(TEST_MEMBER);
        memberRepository.save(TEST_MEMBER2);

        // test event 등록
        eventRepository.save(TEST_EVENT);
        eventRepository.save(TEST_EVENT2);
    }
}
