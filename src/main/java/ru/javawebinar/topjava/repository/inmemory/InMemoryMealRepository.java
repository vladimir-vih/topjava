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
        MealsUtil.meals.forEach(meal -> {
            switch (meal.getDate().getDayOfMonth()) {
                case 30:
                    save(meal, 1);
                    break;
                case 31:
                    save(meal, 2);
                    break;
            }
        });
    }

    @Override
    public Meal save(Meal meal, int userId) {
        AtomicReference<Meal> mealRef = new AtomicReference<>();
        repository.compute(userId, (userIdKey, userMeals) -> {
            if (userMeals == null) userMeals = new ConcurrentHashMap<>();
            if (meal.isNew()) meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
            mealRef.set(meal);
            return userMeals;
        });
        return mealRef.get();
    }

    @Override
    public boolean delete(int mealId, int userId) {
        AtomicBoolean result = new AtomicBoolean(false);
        repository.computeIfPresent(userId, (userIdKey, userMeals) -> {
            userMeals.computeIfPresent(mealId, (id, meal) -> {
                result.set(true);
                return null;
            });
            return userMeals;
        });
        return result.get();
    }

    @Override
    public Meal get(int mealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return null;
        return userMeals.get(mealId);
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

