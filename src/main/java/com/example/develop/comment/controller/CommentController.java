package com.example.develop.comment.controller;

import com.example.develop.comment.dto.request.CommentSaveRequestDto;
import com.example.develop.comment.dto.request.CommentUpdateRequestDto;
import com.example.develop.comment.dto.response.CommentResponseDto;
import com.example.develop.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/schedules/{scheduleId}/comments")
    public ResponseEntity<CommentResponseDto> save(
            @SessionAttribute(name = "LOGIN_USER") Long userId,
            @PathVariable Long scheduleId,
            @RequestBody CommentSaveRequestDto dto
    ) {
        return ResponseEntity.ok(commentService.save(userId, scheduleId, dto));
    }

    @GetMapping("/schedules/{scheduleId}/comments")
    public ResponseEntity<List<CommentResponseDto>> findBySchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(commentService.findBySchedule(scheduleId));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findOne(id));
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> update(
            @SessionAttribute(name = "LOGIN_USER") Long userId,
            @PathVariable Long id,
            @RequestBody CommentUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(commentService.update(id, userId, dto));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(
            @SessionAttribute(name = "LOGIN_USER") Long userId,
            @PathVariable Long id
    ) {
        commentService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
