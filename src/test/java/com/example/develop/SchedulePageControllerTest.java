package com.example.develop;

import com.example.develop.comment.dto.request.CommentSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.user.dto.request.UserSaveRequestDto;
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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SchedulePageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private MockHttpSession session;

    @BeforeAll
    public void initUserAndSchedules() throws Exception {
        UserSaveRequestDto userSaveDto = new UserSaveRequestDto();
        ReflectionTestUtils.setField(userSaveDto, "userName", "Page Tester");
        ReflectionTestUtils.setField(userSaveDto, "email", "page_tester@example.com");
        ReflectionTestUtils.setField(userSaveDto, "password", "password");
        String userJson = objectMapper.writeValueAsString(userSaveDto);
        String userResponse = mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("회원가입 결과: " + result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        UserResponseDto userResp = objectMapper.readValue(userResponse, UserResponseDto.class);
        userId = userResp.getId();

        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);

        // 일정 15건 생성
        for (int i = 1; i <= 15; i++) {
            ScheduleSaveRequestDto scheduleDto = new ScheduleSaveRequestDto();
            ReflectionTestUtils.setField(scheduleDto, "title", "Title " + i);
            ReflectionTestUtils.setField(scheduleDto, "content", "Content " + i);
            String scheduleJson = objectMapper.writeValueAsString(scheduleDto);
            mockMvc.perform(post("/schedules")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(scheduleJson))
                    .andExpect(status().isOk());
        }

        // 댓글 생성
        // 일정 15: 댓글 2건
        for (int j = 1; j <= 2; j++) {
            CommentSaveRequestDto commentDto = new CommentSaveRequestDto();
            ReflectionTestUtils.setField(commentDto, "content", "Comment " + UUID.randomUUID() + " for schedule 15");
            String commentJson = objectMapper.writeValueAsString(commentDto);
            mockMvc.perform(post("/schedules/15/comments")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(commentJson))
                    .andExpect(status().isOk());
        }
        // 일정 14: 댓글 1건
        {
            CommentSaveRequestDto commentDto = new CommentSaveRequestDto();
            ReflectionTestUtils.setField(commentDto, "content", "Comment for schedule 14");
            String commentJson = objectMapper.writeValueAsString(commentDto);
            mockMvc.perform(post("/schedules/14/comments")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(commentJson))
                    .andExpect(status().isOk());
        }
        // 일정 13: 댓글 1건
        {
            CommentSaveRequestDto commentDto = new CommentSaveRequestDto();
            ReflectionTestUtils.setField(commentDto, "content", "Comment for schedule 13");
            String commentJson = objectMapper.writeValueAsString(commentDto);
            mockMvc.perform(post("/schedules/13/comments")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(commentJson))
                    .andExpect(status().isOk());
        }
    }

    @BeforeEach
    public void setupSession() {
        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);
    }

    @Test
    public void testSchedulePage() throws Exception {
        mockMvc.perform(get("/schedules/page")
                        .param("page", "1")
                        .param("size", "10")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.content[0].title", notNullValue()))
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    Object jsonObject = objectMapper.readValue(json, Object.class);
                    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                    System.out.println("Page Result: " + prettyJson);
                });
    }
}
