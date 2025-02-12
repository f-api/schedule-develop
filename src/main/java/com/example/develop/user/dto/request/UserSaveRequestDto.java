package com.example.develop.user.dto.request;

import lombok.Getter;

@Getter
public class UserSaveRequestDto {
    private String userName;
    private String email;
    private String password;
}
