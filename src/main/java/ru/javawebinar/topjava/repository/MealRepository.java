package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeFilterEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(Meal meal, int userId);

    // false if meal does not belong to userId
    boolean delete(int mealId, int userId);

    // null if meal does not belong to userId
    Meal get(int mealId, int userId);

    // ORDERED dateTime desc
    Collection<Meal> getAll(int userId);

    List<Meal> getFilteredByDateTime(int userId, Map<DateTimeFilterEnum, String> filter);
}
