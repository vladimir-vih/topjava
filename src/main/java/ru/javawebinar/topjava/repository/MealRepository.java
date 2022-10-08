package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(Meal meal, int userId);

    // false if meal does not belong to userId
    boolean delete(int mealId, int userId);

    // null if meal does not belong to userId
    Meal get(int mealId, int userId);

    // ORDERED dateTime desc
    List<Meal> getAll(int userId);

    List<Meal> getFilteredByDateTime(int userId, LocalDateTime dateFrom, LocalDateTime dateTo);
}
