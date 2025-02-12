package com.example.develop.user.entity;

import com.example.develop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    @Column(unique = true)
    private String email;
    private String password;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    private User(Long id) {
        this.id = id;
    }

    public static User fromUserId(Long id) {
        return new User(id);
    }

    public void update(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
