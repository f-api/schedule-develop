package com.example.develop;

import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
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
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private MockHttpSession session;

    @BeforeAll
    public void initUser() throws Exception {
        // 회원가입 한 번만 수행하여 테스트용 사용자를 생성
        com.example.develop.user.dto.request.UserSaveRequestDto userSaveDto =
                new com.example.develop.user.dto.request.UserSaveRequestDto();
        ReflectionTestUtils.setField(userSaveDto, "userName", "Schedule Tester");
        ReflectionTestUtils.setField(userSaveDto, "email", "schedule_tester@example.com");
        ReflectionTestUtils.setField(userSaveDto, "password", "password");

        String userJson = objectMapper.writeValueAsString(userSaveDto);
        String userResponse = mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();

        UserResponseDto userResponseDto = objectMapper.readValue(userResponse, UserResponseDto.class);
        userId = userResponseDto.getId();
    }

    @BeforeEach
    public void setupSession() {
        session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", userId);
    }

    @Test
    public void testSaveSchedule() throws Exception {
        ScheduleSaveRequestDto saveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "title", "Test Schedule Title");
        ReflectionTestUtils.setField(saveDto, "content", "Test Schedule Content");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);

        mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Test Schedule Title")))
                .andExpect(jsonPath("$.content", is("Test Schedule Content")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindAllSchedules() throws Exception {
        // 스케줄 하나 생성
        ScheduleSaveRequestDto saveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "title", "List Title");
        ReflectionTestUtils.setField(saveDto, "content", "List Content");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));

        // 전체 조회
        mockMvc.perform(get("/schedules").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testFindOneSchedule() throws Exception {
        // 스케줄 생성
        ScheduleSaveRequestDto saveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "title", "Single Title");
        ReflectionTestUtils.setField(saveDto, "content", "Single Content");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();

        ScheduleSaveResponseDto savedSchedule = objectMapper.readValue(response, ScheduleSaveResponseDto.class);
        Long scheduleId = savedSchedule.getId();

        // 단건 조회
        mockMvc.perform(get("/schedules/" + scheduleId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(scheduleId.intValue())))
                .andExpect(jsonPath("$.title", is("Single Title")))
                .andExpect(jsonPath("$.content", is("Single Content")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testUpdateSchedule() throws Exception {
        // 스케줄 생성
        ScheduleSaveRequestDto saveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "title", "Initial Title");
        ReflectionTestUtils.setField(saveDto, "content", "Initial Content");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        ScheduleSaveResponseDto savedSchedule = objectMapper.readValue(response, ScheduleSaveResponseDto.class);
        Long scheduleId = savedSchedule.getId();

        // 업데이트
        com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto updateDto =
                new com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto();
        ReflectionTestUtils.setField(updateDto, "title", "Updated Title");
        ReflectionTestUtils.setField(updateDto, "content", "Updated Content");
        String updateJson = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/schedules/" + scheduleId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(scheduleId.intValue())))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.content", is("Updated Content")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testDeleteSchedule() throws Exception {
        // 스케줄 생성
        ScheduleSaveRequestDto saveDto = new ScheduleSaveRequestDto();
        ReflectionTestUtils.setField(saveDto, "title", "Title to Delete");
        ReflectionTestUtils.setField(saveDto, "content", "Content to Delete");
        String jsonPayload = objectMapper.writeValueAsString(saveDto);
        String response = mockMvc.perform(post("/schedules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn().getResponse().getContentAsString();
        ScheduleSaveResponseDto savedSchedule = objectMapper.readValue(response, ScheduleSaveResponseDto.class);
        Long scheduleId = savedSchedule.getId();

        // 삭제
        mockMvc.perform(delete("/schedules/" + scheduleId).session(session))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
