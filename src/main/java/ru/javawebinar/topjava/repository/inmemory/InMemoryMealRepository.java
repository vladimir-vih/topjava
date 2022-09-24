package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeFilterEnum;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeFilterEnum.*;
import static ru.javawebinar.topjava.util.DateTimeUtil.*;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUserId(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Meal found = repository.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            if (userId == oldMeal.getUserId()) {
                return meal;
            }
            return oldMeal;
        });
        return found != null && userId == found.getUserId() ? found : null;
    }

    @Override
    public boolean delete(int mealId, int userId) {
        return repository.computeIfPresent(mealId, (id, meal) -> {
            if (userId == meal.getUserId()) return null;
            return meal;
        }) == null;
    }

    @Override
    public Meal get(int mealId, int userId) {
        Meal meal = repository.get(mealId);
        return meal != null && userId == meal.getUserId() ? meal : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values().stream()
                .parallel()
                .filter(meal -> userId == meal.getUserId())
                .sorted(Comparator.comparing(Meal::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getFilteredByDateTime(int userId, Map<DateTimeFilterEnum, String> filter) {
        LocalDateTime dateFrom = filter.containsKey(DATE_FROM) ?
                LocalDate.parse(filter.get(DATE_FROM), DATE_FORMATTER).atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime dateTo = filter.containsKey(DATE_TO) ?
                LocalDate.parse(filter.get(DATE_TO), DATE_FORMATTER).atStartOfDay().plusDays(1) : LocalDateTime.MAX;
        LocalTime timeFrom = filter.containsKey(TIME_FROM) ?
                LocalTime.parse(filter.get(TIME_FROM), TIME_FORMATTER) : LocalTime.MIN;
        LocalTime timeTo = filter.containsKey(TIME_TO) ?
                LocalTime.parse(filter.get(TIME_TO), TIME_FORMATTER) : LocalTime.MAX;
        Collection<Meal> meals = getAll(userId);
        return meals.stream()
                .parallel()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), dateFrom, dateTo))
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getTime(), timeFrom, timeTo))
                .collect(Collectors.toList());
    }
}

