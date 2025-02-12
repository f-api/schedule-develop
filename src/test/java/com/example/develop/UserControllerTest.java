package com.example.develop;

import com.example.develop.user.dto.request.UserSaveRequestDto;
import com.example.develop.user.dto.request.UserUpdateRequestDto;
import com.example.develop.user.dto.response.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private MockHttpSession session;

    @BeforeAll
    public void initUser() throws Exception {
        UserSaveRequestDto userSaveDto = new UserSaveRequestDto();
        ReflectionTestUtils.setField(userSaveDto, "userName", "User Controller Tester");
        ReflectionTestUtils.setField(userSaveDto, "email", "user_controller@example.com");
        ReflectionTestUtils.setField(userSaveDto, "password", "pass123");

        String userJson = objectMapper.writeValueAsString(userSaveDto);
        String response = mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();

        UserResponseDto userResponse = objectMapper.readValue(response, UserResponseDto.class);
        userId = userResponse.getId();
    }

    @BeforeEach
    public void setupSession() {
        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);
    }

    @Test
    public void testSignup() throws Exception {
        UserSaveRequestDto newUserDto = new UserSaveRequestDto();
        // 새로운 이메일을 사용하여 중복 가입 에러 발생을 피함
        ReflectionTestUtils.setField(newUserDto, "userName", "New User");
        ReflectionTestUtils.setField(newUserDto, "email", "new_user_" + System.currentTimeMillis() + "@example.com");
        ReflectionTestUtils.setField(newUserDto, "password", "newpass");

        String newUserJson = objectMapper.writeValueAsString(newUserDto);
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userName", is("New User")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(get("/users").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindOneUser() throws Exception {
        mockMvc.perform(get("/users/" + userId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.userName", is("User Controller Tester")))
                .andExpect(jsonPath("$.email", is("user_controller@example.com")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        ReflectionTestUtils.setField(updateDto, "userName", "Updated User");
        ReflectionTestUtils.setField(updateDto, "email", "updated_user@example.com");
        ReflectionTestUtils.setField(updateDto, "password", "updatedpass");

        String updateJson = objectMapper.writeValueAsString(updateDto);
        mockMvc.perform(put("/users/me")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated_user@example.com")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/me").session(session))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
