package com.example.develop.comment.service;

import com.example.develop.comment.dto.request.CommentSaveRequestDto;
import com.example.develop.comment.dto.request.CommentUpdateRequestDto;
import com.example.develop.comment.dto.response.CommentResponseDto;
import com.example.develop.comment.entity.Comment;
import com.example.develop.comment.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto save(Long userId, Long scheduleId, CommentSaveRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        Comment comment = new Comment(schedule, user, dto.getContent());
        commentRepository.save(comment);
        return new CommentResponseDto(
                comment.getId(),
                user.getId(),
                schedule.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findBySchedule(Long scheduleId) {
        List<Comment> comments = commentRepository.findByScheduleId(scheduleId);
        return comments.stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getSchedule().getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponseDto findOne(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getSchedule().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    @Transactional
    public CommentResponseDto update(Long commentId, Long userId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.update(dto.getContent());
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getSchedule().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }
}
