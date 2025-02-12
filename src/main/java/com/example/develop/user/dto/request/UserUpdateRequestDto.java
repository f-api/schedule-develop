package com.example.develop.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    private String userName;
    private String email;
    private String password;
}
