package com.example.develop;

import com.example.develop.user.dto.request.LoginRequestDto;
import com.example.develop.user.dto.request.UserSaveRequestDto;
import com.example.develop.user.dto.response.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // 회원가입은 테스트 시작 시 한 번만 수행
    @BeforeAll
    public void setup() throws Exception {
        UserSaveRequestDto userSaveDto = new UserSaveRequestDto();
        ReflectionTestUtils.setField(userSaveDto, "userName", "Login Tester");
        ReflectionTestUtils.setField(userSaveDto, "email", "login_tester@example.com");
        ReflectionTestUtils.setField(userSaveDto, "password", "secret");

        String userJson = objectMapper.writeValueAsString(userSaveDto);
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto();
        ReflectionTestUtils.setField(loginDto, "email", "login_tester@example.com");
        ReflectionTestUtils.setField(loginDto, "password", "secret");

        String loginJson = objectMapper.writeValueAsString(loginDto);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testLoginFailure() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto();
        ReflectionTestUtils.setField(loginDto, "email", "login_tester@example.com");
        ReflectionTestUtils.setField(loginDto, "password", "wrongpassword");

        String loginJson = objectMapper.writeValueAsString(loginDto);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().is4xxClientError())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testLogout() throws Exception {
        // 먼저 로그인하여 세션 생성
        LoginRequestDto loginDto = new LoginRequestDto();
        ReflectionTestUtils.setField(loginDto, "email", "login_tester@example.com");
        ReflectionTestUtils.setField(loginDto, "password", "secret");
        String loginJson = objectMapper.writeValueAsString(loginDto);

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getRequest().getSession();

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 성공"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
