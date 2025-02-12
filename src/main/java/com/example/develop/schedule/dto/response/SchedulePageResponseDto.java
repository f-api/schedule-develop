package com.example.develop.schedule.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class SchedulePageResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final int commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String userName;

    public SchedulePageResponseDto(Long id, String title, String content, int commentCount,
                                   LocalDateTime createdAt, LocalDateTime updatedAt, String userName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userName = userName;
    }
}
