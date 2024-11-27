package com.noob.example.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
@Getter
public enum BookCarStatus {

    PENDING(0L),
    APPROVED(1L),
    REJECTED(2L);

    @JsonValue
    private Long value;

    private BookCarStatus(Long value) {
        this.value = value;
    }


    @JsonCreator
    public static BookCarStatus forValue(Long value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }
}
