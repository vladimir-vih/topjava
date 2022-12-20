package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getUserWithMeals() {
        User user = service.getWithMeals(USER_ID);
        USER_WITH_MEALS_MATCHER.assertMatch(user, userWithMeals);
    }

    @Test
    public void getUserWithMealNotFound(){
        assertThrows(NotFoundException.class, () -> service.getWithMeals(NOT_FOUND));
    }

    @Test
    public void getUserWithMealNoMeals(){
        USER_WITH_MEALS_MATCHER.assertMatch(service.getWithMeals(GUEST_ID), guest);
    }
}
