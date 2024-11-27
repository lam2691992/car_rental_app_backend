package com.noob.example.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
@Getter
public enum UserRole {

    ADMIN(0L),
    CUSTOMER(1L);

    @JsonValue
    private Long value;

    private UserRole(Long value) {
        this.value = value;
    }

    @JsonCreator
    public static UserRole forValue(Long value) {
        if (value == null) {
            return null; // Chấp nhận null
        }

        return Arrays.stream(values())
                .filter(role -> role.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }
}
