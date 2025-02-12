package com.example.develop.user.controller;

import com.example.develop.user.dto.request.UserSaveRequestDto;
import com.example.develop.user.dto.request.UserUpdateRequestDto;
import com.example.develop.user.dto.response.UserResponseDto;
import com.example.develop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> save(@RequestBody UserSaveRequestDto dto) {
        return ResponseEntity.ok(userService.save(dto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findOne(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @RequestBody UserUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
