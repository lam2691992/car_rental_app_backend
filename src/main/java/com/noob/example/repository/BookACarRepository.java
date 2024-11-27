package com.noob.example.repository;

import com.noob.example.dto.BookACarDto;
import com.noob.example.entity.BookACar;
import com.noob.example.enums.BookCarStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookACarRepository extends JpaRepository<BookACar, Long> {
    List<BookACar> findAllByUserId(Long userId);

    List<BookACar> findAll(Specification<BookACar> spec, Sort sort);

    boolean existsByCarIdAndBookCarStatus(Long id, BookCarStatus bookCarStatus);
}
