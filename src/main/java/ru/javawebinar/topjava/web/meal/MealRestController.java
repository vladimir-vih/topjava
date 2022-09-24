package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeFilterEnum;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.UnAuthorizedAccessException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;
import java.util.Map;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to create the meal for the user {}", userId);
        return service.create(meal, userId);
    }

    public Meal get(int mealId) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to get the meal {} for the user {}", mealId, userId);
        return service.get(mealId, userId);
    }

    public void update(int mealId, Meal meal) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to update the meal {} for the user {}", meal.getId(), userId);
        if (userId != meal.getUserId()) {
            log.debug("User {} is not authorized to access the meal {}", userId, mealId);
            throw new UnAuthorizedAccessException("User " + userId + " is not authorized to access the meal " + mealId);
        }
        service.update(meal, userId);
    }

    public void delete(int mealId) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to delete the meal {} for the user {}", mealId, userId);
        service.delete(mealId, userId);
    }

    public List<MealTo> getAll() {
        int userId = SecurityUtil.authUserId();
        log.info("The request to get all the meals for the user {}", userId);
        return MealsUtil.getTos(service.getAll(userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public List<MealTo> getAllFiltered(Map<DateTimeFilterEnum, String> filter) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to get FILTERED meals for the user {}", userId);
        return MealsUtil.getTos(service.getAllFiltered(userId, filter), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }
}