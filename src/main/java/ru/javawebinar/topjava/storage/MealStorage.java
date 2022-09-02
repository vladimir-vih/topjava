package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    int add(Meal m);

    Meal get(Integer id);

    List<Meal> getAll();

    void update(Meal m);

    void delete(Integer id);

    int size();

    void clear();


}
