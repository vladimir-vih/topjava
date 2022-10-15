package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals1.forEach(meal -> save(meal, 1));
        MealsUtil.meals2.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        AtomicReference<Meal> mealRef = new AtomicReference<>();
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            Map<Integer, Meal> userMeals = repository.get(userId);
            if (userMeals == null) {
                userMeals = new ConcurrentHashMap<>();
                repository.put(userId, userMeals);
            }
            userMeals.computeIfAbsent(meal.getId(),
                    (mealId) -> {
                        mealRef.set(meal);
                        return meal;
                    });
        } else {
            repository.get(userId).computeIfPresent(meal.getId(),
                    (mealId, oldMeal) -> {
                        mealRef.set(meal);
                        return meal;
                    });
        }
        return mealRef.get();
    }

    @Override
    public boolean delete(int mealId, int userId) {
        AtomicBoolean result = new AtomicBoolean(false);
        repository.get(userId).computeIfPresent(mealId, (id, meal) -> {
            result.set(true);
            return null;
        });
        return result.get();
    }

    @Override
    public Meal get(int mealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals == null ? null : userMeals.get(mealId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getFilteredByDateTime(int userId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return getFilteredByPredicate(userId,
                meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), dateFrom, dateTo));
    }

    private List<Meal> getFilteredByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return Collections.emptyList();
        return userMeals.values().stream()
                .parallel()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

