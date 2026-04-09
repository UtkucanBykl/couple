package com.example.couple.repository;

import com.example.couple.entity.Couple;
import com.example.couple.entity.Day;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    Page<Day> findAllByCoupleId(Long coupleId, Pageable pageable);

    boolean existsDayByCoupleAndDate(Couple couple, LocalDate date);
}
