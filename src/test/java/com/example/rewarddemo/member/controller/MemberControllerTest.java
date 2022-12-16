package com.example.rewarddemo.member.controller;

import com.example.rewarddemo.member.controller.dto.MemberRequest.MemberJoinRequest;
import com.example.rewarddemo.member.controller.dto.MemberRequest.MemberLoginRequest;
import com.example.rewarddemo.member.entity.Member;
import com.example.rewarddemo.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    private static final String MEMBER_API_PATH = "/v1/members";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 test - id 없는 경우 badRequest")
    public void memberJoinTest_idEmpty_BadRequest() throws Exception {
        String password = "testPassword333";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(null, password);

        mockMvc.perform(post(MEMBER_API_PATH + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("회원 가입 test - password 없는 경우 badRequest")
    public void memberJoinTest_passwordEmpty_BadRequest() throws Exception {
        String memberId = "testMember";
        String password = null;
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(memberId, password);

        mockMvc.perform(post(MEMBER_API_PATH + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("회원 가입 test - 이미 존재하는 id인 경우 badRequest")
    public void memberJoinTest_AlreadyExistMemberId_BadRequest() throws Exception {
        String memberId = "testId";
        String password1 = "test1";
        String password2 = "test2";

        Member member = new Member(memberId, password1);
        memberRepository.save(member);

        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(memberId, password2);

        mockMvc.perform(post(MEMBER_API_PATH + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("회원 가입 test")
    public void memberJoinTest() throws Exception {
        String memberId = "testId333";
        String password = "testPassword333";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(memberId, password);

        mockMvc.perform(post(MEMBER_API_PATH + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("memberNo").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-member").exists())
        ;
    }

    @Test
    @DisplayName("회원정보 조회 Test")
    public void queryMemberTest() throws Exception {
        // given
        String memberId = "testId456";
        String password = "test1";

        Member member = new Member(memberId, password);
        Member savedMember = memberRepository.save(member);

        // when
        mockMvc.perform(get(MEMBER_API_PATH + "/{memberNo}", savedMember.getNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("memberNo").exists())
                .andExpect(jsonPath("memberId").exists())
                .andExpect(jsonPath("point").exists())
        ;
    }

    @Test
    @DisplayName("회원정보 조회 Test - wrong memberNo")
    public void queryMemberTest_wrongMemberNo() throws Exception {
        // when
        mockMvc.perform(get(MEMBER_API_PATH + "/{memberNo}", 99999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("로그인 test")
    public void loginTest() throws Exception {
        // given
        String memberId = "testId457";
        String password = "test1";

        Member member = new Member(memberId, password);
        memberRepository.save(member);

        MemberLoginRequest loginRequest = new MemberLoginRequest(memberId, password);

        // when & then
        mockMvc.perform(post(MEMBER_API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("memberNo").exists())
                .andExpect(jsonPath("memberId").exists())
                .andExpect(jsonPath("point").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-member").exists())
        ;
    }


    @Test
    @DisplayName("로그인 test - 비밀번호 불일치")
    public void loginTest_loginFail() throws Exception {
        // given
        String memberId = "testId469";
        String password = "test1";

        Member member = new Member(memberId, password);
        memberRepository.save(member);

        MemberLoginRequest loginRequest = new MemberLoginRequest(memberId, password + "123123");

        // when & then
        mockMvc.perform(post(MEMBER_API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }


    @Test
    @DisplayName("로그인 test - id 찾을수 없음")
    public void loginTest_nonexistentId() throws Exception {
        // given
        String memberId = "nonexistentId";
        String password = "test1";

        MemberLoginRequest loginRequest = new MemberLoginRequest(memberId, password);

        // when & then
        mockMvc.perform(post(MEMBER_API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("message").exists())
        ;
    }
}