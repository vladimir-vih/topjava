package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.exception.NotExistStorageException;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class MealMapStorage implements MealStorage {
    private static MealMapStorage INSTANCE;
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final AtomicInteger mealIndex = new AtomicInteger(0);

    private MealMapStorage() {
        MealsUtil.meals.forEach(this::add);
    }

    public static MealMapStorage getInstance() {
        if (INSTANCE == null) {
            synchronized (MealMapStorage.class) {
                INSTANCE = new MealMapStorage();
            }
        }
        return INSTANCE;
    }

    @Override
    public int add(Meal m) {
        Integer id = mealIndex.incrementAndGet();
        m.setId(id);
        storage.put(id, m);
        return id;
    }

    @Override
    public Meal get(Integer id) {
        chekMealExist(id);
        return storage.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Meal m) {
        chekMealExist(m.getId());
        storage.put(m.getId(),m);
    }

    private void chekMealExist(Integer id) {
        if (!storage.containsKey(id)) {
            throw new NotExistStorageException();
        }
    }

    @Override
    public void delete(Integer id) {
        chekMealExist(id);
        storage.remove(id);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
