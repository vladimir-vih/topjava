package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    Meal add(Meal m);

    Meal get(int id);

    List<Meal> getAll();

    Meal update(Meal m);

    boolean delete(int id);
}
