package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MealsUtil {
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static final List<Meal> meals = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0),
                    "Завтрак", 500, 1),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0),
                    "Обед", 1000, 1),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0),
                    "Ужин", 500, 1),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),
                    "Еда на граничное значение", 100, 2),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0),
                    "Завтрак", 1000, 2),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0),
                    "Обед", 500, 2),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0),
                    "Ужин", 410, 2)
    );

    public static List<MealTo> getTos(Collection<Meal> meals, int caloriesPerDay) {
        return filterByPredicate(meals, caloriesPerDay, meal -> true);
    }

    public static List<MealTo> getFilteredTos(Collection<Meal> meals, int caloriesPerDay, LocalTime startTime,
                                              LocalTime endTime) {
        List<MealTo> mealTos = getTos(meals, caloriesPerDay);
        return mealTos.stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    private static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
                );

        return meals.stream()
                .filter(filter)
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
