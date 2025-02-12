package com.example.develop.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentSaveRequestDto {

    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    private String content;
}
