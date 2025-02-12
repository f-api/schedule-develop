package com.example.develop;

import com.example.develop.comment.dto.request.CommentSaveRequestDto;
import com.example.develop.comment.dto.request.CommentUpdateRequestDto;
import com.example.develop.comment.dto.response.CommentResponseDto;
import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private Long scheduleId;
    private MockHttpSession session;

    @BeforeAll
    public void setupUserAndSchedule() throws Exception {
        // 사용자 생성
        UserSaveRequestDto userSaveDto = new UserSaveRequestDto();
        ReflectionTestUtils.setField(userSaveDto, "userName", "Comment Tester");
        ReflectionTestUtils.setField(userSaveDto, "email", "comment_tester@example.com");
        ReflectionTestUtils.setField(userSaveDto, "password", "password");
        String userJson = objectMapper.writeValueAsString(userSaveDto);
        String userResponse = mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserResponseDto userResp = objectMapper.readValue(userResponse, UserResponseDto.class);
        userId = userResp.getId();

        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);

        // 일정 생성
        ScheduleSaveRequestDto scheduleSaveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(scheduleSaveDto, "title", "Schedule for Comments");
        ReflectionTestUtils.setField(scheduleSaveDto, "content", "Schedule Content");
        String scheduleJson = objectMapper.writeValueAsString(scheduleSaveDto);
        String scheduleResponse = mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ScheduleSaveResponseDto scheduleResp = objectMapper.readValue(scheduleResponse, ScheduleSaveResponseDto.class);
        scheduleId = scheduleResp.getId();
    }

    @BeforeEach
    public void setupSession() {
        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);
    }

    @Test
    public void testSaveComment() throws Exception {
        CommentSaveRequestDto saveDto = new CommentSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "content", "This is a comment.");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);

        mockMvc.perform(post("/schedules/" + scheduleId + "/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content", is("This is a comment.")))
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.scheduleId", is(scheduleId.intValue())))
                .andDo(result -> System.out.println("Save Comment: " + result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindByScheduleComments() throws Exception {
        // 댓글 하나 생성
        CommentSaveRequestDto saveDto = new CommentSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "content", "Comment for list");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        mockMvc.perform(post("/schedules/" + scheduleId + "/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());

        // 전체 조회
        mockMvc.perform(get("/schedules/" + scheduleId + "/comments")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andDo(result -> System.out.println("FindBySchedule Comments: " + result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindOneComment() throws Exception {
        // 댓글 생성
        CommentSaveRequestDto saveDto = new CommentSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "content", "Single Comment");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules/" + scheduleId + "/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Save for single: " + result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        CommentResponseDto savedComment = objectMapper.readValue(response, CommentResponseDto.class);
        Long commentId = savedComment.getId();

        // 단건 조회
        mockMvc.perform(get("/comments/" + commentId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentId.intValue())))
                .andExpect(jsonPath("$.content", is("Single Comment")))
                .andDo(result -> System.out.println("Find One Comment: " + result.getResponse().getContentAsString()));
    }

    @Test
    public void testUpdateComment() throws Exception {
        // 댓글 생성
        CommentSaveRequestDto saveDto = new CommentSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "content", "Old Comment");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules/" + scheduleId + "/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Save for update: " + result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        CommentResponseDto savedComment = objectMapper.readValue(response, CommentResponseDto.class);
        Long commentId = savedComment.getId();

        // 업데이트
        CommentUpdateRequestDto updateDto = new CommentUpdateRequestDto();
        ReflectionTestUtils.setField(updateDto, "content", "Updated Comment");
        String updateJson = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/comments/" + commentId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Updated Comment")))
                .andDo(result -> System.out.println("Update Comment: " + result.getResponse().getContentAsString()));
    }

    @Test
    public void testDeleteComment() throws Exception {
        // 댓글 생성
        CommentSaveRequestDto saveDto = new CommentSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "content", "Comment to Delete");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules/" + scheduleId + "/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Save for delete: " + result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        CommentResponseDto savedComment = objectMapper.readValue(response, CommentResponseDto.class);
        Long commentId = savedComment.getId();

        // 삭제
        mockMvc.perform(delete("/comments/" + commentId).session(session))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Delete Comment: " + result.getResponse().getContentAsString()));
    }
}
