package com.example.develop.comment.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private final Long id;
    private final Long userId;
    private final Long scheduleId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CommentResponseDto(Long id, Long userId, Long scheduleId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
