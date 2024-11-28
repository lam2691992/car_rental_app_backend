package com.noob.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_notes")
public class CarNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false) // Khóa ngoại liên kết tới bảng cars
    private Car car;

    @Column(name = "note", nullable = false)
    private String note;
}


