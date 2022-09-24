package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeFilterEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.javawebinar.topjava.util.ValidationUtil.*;

@Service
public class MealService {
    @Autowired
    private MealRepository repository;

    public Meal create(Meal meal, int userId) {
        checkNew(meal);
        return repository.save(meal, userId);
    }

    public Meal get(int mealId, int userId) {
        return checkNotFoundWithId(repository.get(mealId, userId), mealId);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

    public void delete(int mealId, int userId) {
        checkNotFound(repository.delete(mealId, userId), String.valueOf(mealId));
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public List<Meal> getAllFiltered(int userId, Map<DateTimeFilterEnum, String> filter) {
        return repository.getFilteredByDateTime(userId, filter);
    }
}