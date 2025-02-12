package com.example.develop.user.service;

import com.example.develop.user.dto.request.UserSaveRequestDto;
import com.example.develop.user.dto.request.UserUpdateRequestDto;
import com.example.develop.user.dto.response.UserResponseDto;
import com.example.develop.user.entity.User;
import com.example.develop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto save(UserSaveRequestDto dto) {
        User user = new User(dto.getUserName(), dto.getEmail(), dto.getPassword());
        userRepository.save(user);
        return new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findOne(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        return new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        user.update(dto.getUserName(), dto.getEmail(), dto.getPassword());
        return new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
