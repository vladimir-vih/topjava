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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = repository.getOrDefault(userId, new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setUserId(userId);
            meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
            repository.put(userId, userMeals);
            return meal;
        }
        // handle case: update, but not present in storage
        Meal found = userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            if (userId == oldMeal.getUserId()) {
                meal.setUserId(userId);
                return meal;
            }
            return oldMeal;
        });
        repository.put(userId, userMeals);
        return found;
    }

    @Override
    public boolean delete(int mealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return false;
        AtomicBoolean result = new AtomicBoolean(false);
        userMeals.computeIfPresent(mealId, (id, meal) -> {
            if (userId == meal.getUserId()) {
                result.getAndSet(true);
                return null;
            }
            return meal;
        });
        return result.get();
    }

    @Override
    public Meal get(int mealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return null;
        Meal meal = userMeals.get(mealId);
        return meal != null && userId == meal.getUserId() ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getFilteredByPredicate(userId, meal -> userId == meal.getUserId());
    }

    @Override
    public List<Meal> getFilteredByDateTime(int userId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return getFilteredByPredicate(userId, meal -> userId == meal.getUserId()
                && DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), dateFrom, dateTo));
    }

    private List<Meal> getFilteredByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return new ArrayList<>();
        return userMeals.values().stream()
                .parallel()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDate).thenComparing(Meal::getTime).reversed())
                .collect(Collectors.toList());
    }
}

