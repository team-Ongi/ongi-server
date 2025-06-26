package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal,Long> {
    List<Meal> findByUserId(Long userId);
    Optional<Meal> findByUserAndMealType(User user, MealType mealType);
}
