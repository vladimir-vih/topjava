package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) {
            userMeals = repository.computeIfAbsent(userId, (meals) -> new ConcurrentHashMap<>());
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
        } else {
            if (userMeals.computeIfPresent(meal.getId(), (mealId, oldMeal) -> meal) == null) return null;
        }
        return meal;
    }

    @Override
    public boolean delete(int mealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && userMeals.remove(mealId) != null;
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

