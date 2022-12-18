package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles("datajpa")
public class MealServiceGetWithUserTest extends MealServiceTest {
    @Test
    public void getMealWithUser() {
        MEAL_MATCHER.assertMatch(service.getMealWithUser(MEAL1_ID, USER_ID), meal1WithUser);
    }
}
