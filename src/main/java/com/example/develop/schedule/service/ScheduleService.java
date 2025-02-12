package com.example.develop.schedule.service;

import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto;
import com.example.develop.schedule.dto.response.ScheduleResponseDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
import com.example.develop.schedule.dto.response.ScheduleUpdateResponseDto;
import com.example.develop.schedule.entity.Schedule;
import com.example.develop.schedule.repository.ScheduleRepository;
import com.example.develop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ScheduleSaveResponseDto save(Long userId, ScheduleSaveRequestDto dto) {
        User user = User.fromUserId(userId);
        Schedule schedule = new Schedule(user, dto.getTitle(), dto.getContent());
        scheduleRepository.save(schedule);
        return new ScheduleSaveResponseDto(
                schedule.getId(),
                user.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAll() {
        return scheduleRepository.findAll().stream()
                .map(schedule -> new ScheduleResponseDto(
                        schedule.getId(),
                        schedule.getUser().getId(),
                        schedule.getTitle(),
                        schedule.getContent(),
                        schedule.getCreatedAt(),
                        schedule.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findOne(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional
    public ScheduleUpdateResponseDto update(Long scheduleId, Long userId, ScheduleUpdateRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));
        if (!userId.equals(schedule.getUser().getId())) {
            throw new IllegalArgumentException("본인이 작성한 스케줄만 수정할 수 있습니다.");
        }
        schedule.update(dto.getTitle(), dto.getContent());
        return new ScheduleUpdateResponseDto(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteById(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));
        if (!userId.equals(schedule.getUser().getId())) {
            throw new IllegalArgumentException("본인이 작성한 스케줄만 삭제할 수 있습니다.");
        }
        scheduleRepository.delete(schedule);
    }
}
