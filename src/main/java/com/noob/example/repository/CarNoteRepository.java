package com.noob.example.repository;

import com.noob.example.entity.CarNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarNoteRepository extends JpaRepository<CarNote, Long> {
    @Query("SELECT c FROM CarNote c WHERE c.car.id = :carId")
    List<CarNote> findByCarId(@Param("carId") Long carId);
}
