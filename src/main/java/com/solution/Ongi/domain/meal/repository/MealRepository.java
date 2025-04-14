package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal,Long> {
    List<Meal> findByUserId(Long userId);
}
