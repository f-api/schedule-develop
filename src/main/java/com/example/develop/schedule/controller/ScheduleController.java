package com.example.develop.schedule.controller;

import com.example.develop.common.consts.Const;
import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto;
import com.example.develop.schedule.dto.response.SchedulePageResponseDto;
import com.example.develop.schedule.dto.response.ScheduleResponseDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
import com.example.develop.schedule.dto.response.ScheduleUpdateResponseDto;
import com.example.develop.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleSaveResponseDto> save(
            @SessionAttribute(name = Const.LOGIN_USER) Long userId,
            @RequestBody ScheduleSaveRequestDto dto
    ) {
        return ResponseEntity.ok(scheduleService.save(userId, dto));
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponseDto>> findAll() {
        return ResponseEntity.ok(scheduleService.findAll());
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<ScheduleResponseDto> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findOne(id));
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ScheduleUpdateResponseDto> update(
            @SessionAttribute(name = Const.LOGIN_USER) Long userId,
            @PathVariable Long id,
            @RequestBody ScheduleUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(scheduleService.update(id, userId, dto));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> delete(
            @SessionAttribute(name = Const.LOGIN_USER) Long userId,
            @PathVariable Long id
    ) {
        scheduleService.deleteById(id, userId);
        return ResponseEntity.ok().build();
    }

    // 일정 페이지 API
    @GetMapping("/schedules/page")
    public ResponseEntity<Page<SchedulePageResponseDto>> findAllPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SchedulePageResponseDto> result = scheduleService.findAllPage(page, size);
        return ResponseEntity.ok(result);
    }
}
