package com.example.rewarddemo.events.history.controller;

import com.example.rewarddemo.events.entity.Event;
import com.example.rewarddemo.events.history.controller.dto.EventParticipationRequest.ParticipateRequest;
import com.example.rewarddemo.events.history.entity.EventParticipation;
import com.example.rewarddemo.events.history.repository.EventParticipationRepository;
import com.example.rewarddemo.events.repository.EventRepository;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class EventParticipationControllerTest {

    private static final String PARTICIPATION_API_PATH = "/v1/events/{eventId}/participations";

    private Event event;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipationRepository participationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        participationRepository.deleteAll();
        this.event = eventRepository.findAll().stream().findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("이벤트 참여 test")
    public void participateTest() throws Exception {
        // given
        Member member = member();
        long memberNo = member.getNo();
        ParticipateRequest participateRequest = new ParticipateRequest(memberNo);

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("no").exists())
                .andExpect(jsonPath("rewardAmount").exists())
                .andExpect(jsonPath("continuousDays").exists())
                .andExpect(jsonPath("participatedAt").exists())

                .andExpect(jsonPath("event").exists())
                .andExpect(jsonPath("event.eventId").value(event.getId()))
                .andExpect(jsonPath("event.title").exists())
                .andExpect(jsonPath("event.description").exists())
                .andExpect(jsonPath("event.reward").exists())
                .andExpect(jsonPath("event.reward.no").exists())
                .andExpect(jsonPath("event.reward.amount").exists())
                .andExpect(jsonPath("event.bonusRewards").exists())

                .andExpect(jsonPath("member").exists())
                .andExpect(jsonPath("member.memberNo").exists())
                .andExpect(jsonPath("member.memberId").exists())
                .andExpect(jsonPath("member.point").value(event.getReward().getAmount()))

                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }

    @Test
    @DisplayName("이벤트 참여 test - 10명 이상 참여시, 참여 제한")
    public void participateTest_eventFull() throws Exception {
        // given
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            members.add(member());
        }
        List<EventParticipation> participations = members.stream().map(member ->
                EventParticipation.createParticipation(
                        member,
                        event,
                        1L,
                        100L,
                        LocalDateTime.now())
        ).toList();

        participationRepository.saveAll(participations);

        Member lastMember = member();
        ParticipateRequest participateRequest = new ParticipateRequest(lastMember.getNo());

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("이벤트 참여 test - 당일 참여시, 참여 제한")
    public void participateTest_participateSameDay() throws Exception {
        // given
        Member member = member();
        ParticipateRequest participateRequest = new ParticipateRequest(member.getNo());

        participationRepository.save(participation(member, 1L, LocalDateTime.now()));

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("이벤트 참여 test - 보너스 연속 참여일수 3일")
    public void participateTest_bonusReward3() throws Exception {
        // given
        long expectedRewardAmount = 400L;
        long expectedDays = 3L;
        Member member = member();
        ParticipateRequest participateRequest = new ParticipateRequest(member.getNo());
        var participations = List.of(
                participation(member, 1L, LocalDateTime.now().minusDays(2)),
                participation(member, 2L, LocalDateTime.now().minusDays(1))
        );

        participationRepository.saveAll(participations);

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("no").exists())
                .andExpect(jsonPath("rewardAmount").value(expectedRewardAmount))
                .andExpect(jsonPath("continuousDays").value(expectedDays))
                .andExpect(jsonPath("participatedAt").exists())

                .andExpect(jsonPath("member.point").value(expectedRewardAmount))
        ;
    }

    @Test
    @DisplayName("이벤트 참여 test - 보너스 연속 참여일수 5일")
    public void participateTest_bonusReward5() throws Exception {
        // given
        long expectedRewardAmount = 600L;
        long expectedDays = 5L;
        Member member = member();
        ParticipateRequest participateRequest = new ParticipateRequest(member.getNo());
        var participations = List.of(
                participation(member, 1L, LocalDateTime.now().minusDays(4)),
                participation(member, 2L, LocalDateTime.now().minusDays(3)),
                participation(member, 3L, LocalDateTime.now().minusDays(2)),
                participation(member, 4L, LocalDateTime.now().minusDays(1))
        );

        participationRepository.saveAll(participations);

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("no").exists())
                .andExpect(jsonPath("rewardAmount").value(expectedRewardAmount))
                .andExpect(jsonPath("continuousDays").value(expectedDays))
                .andExpect(jsonPath("participatedAt").exists())

                .andExpect(jsonPath("member.point").value(expectedRewardAmount))
        ;
    }

    @Test
    @DisplayName("이벤트 참여 test - 보너스 연속 참여일수 10일")
    public void participateTest_bonusReward10() throws Exception {
        // given
        long expectedRewardAmount = 1100L;
        long expectedDays = 10L;
        Member member = member();
        ParticipateRequest participateRequest = new ParticipateRequest(member.getNo());
        var participations = List.of(
                participation(member, 1L, LocalDateTime.now().minusDays(9)),
                participation(member, 2L, LocalDateTime.now().minusDays(8)),
                participation(member, 3L, LocalDateTime.now().minusDays(7)),
                participation(member, 4L, LocalDateTime.now().minusDays(6)),
                participation(member, 5L, LocalDateTime.now().minusDays(5)),
                participation(member, 6L, LocalDateTime.now().minusDays(4)),
                participation(member, 7L, LocalDateTime.now().minusDays(3)),
                participation(member, 8L, LocalDateTime.now().minusDays(2)),
                participation(member, 9L, LocalDateTime.now().minusDays(1))
        );

        participationRepository.saveAll(participations);

        // when & then
        mockMvc.perform(post(PARTICIPATION_API_PATH, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(participateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("no").exists())
                .andExpect(jsonPath("rewardAmount").value(expectedRewardAmount))
                .andExpect(jsonPath("continuousDays").value(expectedDays))
                .andExpect(jsonPath("participatedAt").exists())

                .andExpect(jsonPath("member.point").value(expectedRewardAmount))
        ;
    }

    @Test
    @DisplayName("이벤트 참여 이력 목록 조회")
    public void queryParticipations() throws Exception {
        // given
        AtomicInteger timeCount = new AtomicInteger(10);
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            members.add(member());
        }
        List<EventParticipation> participations = members.stream().map(member ->
                EventParticipation.createParticipation(
                        member,
                        event,
                        1L,
                        100L,
                        LocalDateTime.now().minusSeconds(timeCount.getAndIncrement()))

        ).toList();
        participationRepository.saveAll(participations);

        // when
        mockMvc.perform(get(PARTICIPATION_API_PATH, event.getId())
                        .param("participateDate", LocalDate.now().toString())
                        .param("size", "10")
                        .param("sort", "participatedAt,DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_embedded.participations").exists())

                .andExpect(jsonPath("_embedded.participations[0].no").exists())
                .andExpect(jsonPath("_embedded.participations[0].rewardAmount").exists())
                .andExpect(jsonPath("_embedded.participations[0].continuousDays").exists())
                .andExpect(jsonPath("_embedded.participations[0].participatedAt").exists())

                .andExpect(jsonPath("_embedded.participations[0].event").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.eventId").value(event.getId()))
                .andExpect(jsonPath("_embedded.participations[0].event.title").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.description").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.reward").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.reward.no").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.reward.amount").exists())
                .andExpect(jsonPath("_embedded.participations[0].event.bonusRewards").exists())

                .andExpect(jsonPath("_embedded.participations[0].member").exists())
                .andExpect(jsonPath("_embedded.participations[0].member.memberNo").exists())
                .andExpect(jsonPath("_embedded.participations[0].member.memberId").exists())
                .andExpect(jsonPath("_embedded.participations[0].member.point").exists())

                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())

                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.size").value(10))
                .andExpect(jsonPath("page.totalPages").value(1))
                .andExpect(jsonPath("page.number").value(0))
        ;
    }

    private Member member() {
        int randomInt = new Random().nextInt();
        Member member = new Member("testMember" + randomInt, "password");

        return memberRepository.save(member);
    }

    private EventParticipation participation(Member member, long continuousDays, LocalDateTime localDateTime) {
        return EventParticipation.createParticipation(
                member,
                event,
                continuousDays,
                100L,
                localDateTime
        );
    }
}