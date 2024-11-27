package com.noob.example.entity;

import com.noob.example.dto.SignupRequest;
import com.noob.example.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;

    //lớp user có tham số
    public User (SignupRequest signupRequest) {

        this.name = signupRequest.getName();
        this.email = signupRequest.getEmail();
        this.password = signupRequest.getPassword();
        if (signupRequest.getUserRole() == null) {
            this.userRole = UserRole.CUSTOMER;
        }else{
            this.userRole = UserRole.forValue(signupRequest.getUserRole());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
