package com.example.develop.schedule.dto.request;

import lombok.Getter;

@Getter
public class ScheduleSaveRequestDto {

    private Long userId;
    private String title;
    private String content;
}
