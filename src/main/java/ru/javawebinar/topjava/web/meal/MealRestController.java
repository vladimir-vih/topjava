package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        checkArgsNonNull("CREATE", meal);
        int userId = SecurityUtil.authUserId();
        log.info("The request to create the meal for the user {}", userId);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public Meal get(Integer mealId) {
        checkArgsNonNull("GET", mealId);
        int userId = SecurityUtil.authUserId();
        log.info("The request to get the meal {} for the user {}", mealId, userId);
        return service.get(mealId, userId);
    }

    public void update(Integer mealId, Meal meal) {
        checkArgsNonNull("UPDATE", mealId, meal);
        int userId = SecurityUtil.authUserId();
        log.info("The request to update the meal {} for the user {}", mealId, userId);
        assureIdConsistent(meal, mealId);
        service.update(meal, userId);
    }

    public void delete(Integer mealId) {
        checkArgsNonNull("DELETE", mealId);
        int userId = SecurityUtil.authUserId();
        log.info("The request to delete the meal {} for the user {}", mealId, userId);
        service.delete(mealId, userId);
    }

    private void checkArgsNonNull(String methodName, Object... args) {
        for (Object o : args) {
            Objects.requireNonNull(o, "Got NULL object in the " + methodName + " method");
        }
    }

    public List<MealTo> getAll() {
        int userId = SecurityUtil.authUserId();
        log.info("The request to get all the meals for the user {}", userId);
        return MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(LocalDate dateFromRaw, LocalDate dateToRaw, LocalTime timeFromRaw,
                                       LocalTime timeToRaw) {
        int userId = SecurityUtil.authUserId();
        log.info("The request to get FILTERED meals for the user {}", userId);
        final LocalDateTime dateFrom = dateFromRaw != null ? dateFromRaw.atStartOfDay() : LocalDateTime.MIN;
        final LocalDateTime dateTo = dateToRaw != null ? dateToRaw.atStartOfDay().plusDays(1) : LocalDateTime.MAX;
        final LocalTime timeFrom = timeFromRaw != null ? timeFromRaw : LocalTime.MIN;
        final LocalTime timeTo = timeToRaw != null ? timeToRaw : LocalTime.MAX;
        return MealsUtil.getFilteredTos(service.getAllFiltered(userId, dateFrom, dateTo),
                SecurityUtil.authUserCaloriesPerDay(), timeFrom, timeTo);
    }
}