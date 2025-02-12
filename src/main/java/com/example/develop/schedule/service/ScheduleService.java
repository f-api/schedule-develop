package com.example.develop.schedule.service;

import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto;
import com.example.develop.schedule.dto.response.ScheduleResponseDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
import com.example.develop.schedule.dto.response.ScheduleUpdateResponseDto;
import com.example.develop.schedule.entity.Schedule;
import com.example.develop.schedule.repository.ScheduleRepository;
import com.example.develop.user.entity.User;
import com.example.develop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleSaveResponseDto save(ScheduleSaveRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        Schedule schedule = new Schedule(user, dto.getTitle(), dto.getContent());
        scheduleRepository.save(schedule);
        return new ScheduleSaveResponseDto(
                schedule.getId(),
                user.getId(),
                user.getUserName(),
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
                        schedule.getUser().getUserName(),
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
                schedule.getUser().getUserName(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional
    public ScheduleUpdateResponseDto update(Long id, ScheduleUpdateRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));
        schedule.update(dto.getTitle(), dto.getContent());
        return new ScheduleUpdateResponseDto(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getUser().getUserName(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }
}
