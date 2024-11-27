package com.noob.example.dto;

import com.noob.example.entity.User;
import com.noob.example.enums.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;

    public UserDto (User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.userRole = UserRole.valueOf(String.valueOf(user.getUserRole()));
    }
}

