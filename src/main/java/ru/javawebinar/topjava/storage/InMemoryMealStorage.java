package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealStorage implements MealStorage {
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(0);

    public InMemoryMealStorage() {
        MealsUtil.meals.forEach(this::add);
    }   

    @Override
    public Meal add(Meal m) {
        int id = currentId.incrementAndGet();
        m.setId(id);
        storage.put(id, m);
        return m;
    }

    @Override
    public Meal get(int id) {
        return storage.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Meal update(Meal m) {
        final Meal result = storage.replace(m.getId(), m);
        return result == null ? null : m;
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }
}
