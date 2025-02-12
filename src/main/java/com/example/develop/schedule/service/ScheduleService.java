package com.example.develop.schedule.service;

import com.example.develop.comment.dto.CommentCountDto;
import com.example.develop.comment.repository.CommentRepository;
import com.example.develop.schedule.dto.request.ScheduleSaveRequestDto;
import com.example.develop.schedule.dto.request.ScheduleUpdateRequestDto;
import com.example.develop.schedule.dto.response.ScheduleResponseDto;
import com.example.develop.schedule.dto.response.ScheduleSaveResponseDto;
import com.example.develop.schedule.dto.response.ScheduleUpdateResponseDto;
import com.example.develop.schedule.dto.response.SchedulePageResponseDto;
import com.example.develop.schedule.entity.Schedule;
import com.example.develop.schedule.repository.ScheduleRepository;
import com.example.develop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

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
                        schedule.getUpdatedAt()))
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
                schedule.getUpdatedAt());
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
                schedule.getUpdatedAt());
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

    @Transactional(readOnly = true)
    public Page<SchedulePageResponseDto> findAllPage(int page, int size) {
        // 클라이언트에서 1부터 전달된 페이지 번호를 0 기반으로 조정
        int adjustedPage = (page > 0) ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(adjustedPage, size, Sort.by("updatedAt").descending());
        // 1. Schedule Page 조회
        Page<Schedule> schedulePage = scheduleRepository.findAll(pageable);
        // 2. 일정 ID 리스트 추출
        List<Long> scheduleIds = schedulePage.stream()
                .map(Schedule::getId)
                .collect(Collectors.toList());
        // 3. 별도 쿼리로 댓글 수 조회
        List<CommentCountDto> countResults = commentRepository.countByScheduleIds(scheduleIds);
        Map<Long, Long> commentCountMap = countResults.stream()
                .collect(Collectors.toMap(CommentCountDto::getScheduleId, CommentCountDto::getCount));
        // 4. 각 Schedule을 SchedulePageResponseDto로 변환 (댓글 수는 Long을 int로 변환)
        return schedulePage.map(schedule -> new SchedulePageResponseDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                commentCountMap.getOrDefault(schedule.getId(), 0L).intValue(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt(),
                schedule.getUser().getUserName()
        ));
    }
}
