package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = {"classpath:db/initDB.sql", "classpath:db/populateDB.sql"}, config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    private static final Logger log = LoggerFactory.getLogger(MealServiceTest.class);

    @Autowired
    private MealService service;

    @Test
    public void getExist() {
        log.info("do getExist");
        assertMatch(service.get(ID_MEAL1_OF_USER, USER_ID), meal1User);
    }

    @Test
    public void getNotExist() {
        log.info("do getNotExist");
        assertThrows(NotFoundException.class, () -> service.get(TO_ADD_MEAL_ID, USER_ID));
    }

    @Test
    public void getMealOtherUser() {
        log.info("do getNotExist");
        assertThrows(NotFoundException.class, () -> service.get(ID_MEAL1_OF_ADMIN, USER_ID));
    }

    @Test
    public void deleteExist() {
        log.info("do deleteExist");
        service.delete(ID_MEAL1_OF_USER, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(ID_MEAL1_OF_USER, USER_ID));
    }

    @Test
    public void deleteNotExist() {
        log.info("do deleteNotExist");
        assertThrows(NotFoundException.class, () -> service.delete(TO_ADD_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteMealOtherUser() {
        log.info("do deleteMealOtherUser");
        assertThrows(NotFoundException.class, () -> service.delete(ID_MEAL1_OF_ADMIN, USER_ID));
    }

    @Test
    public void getBetweenInclusiveTwoMealsResult() {
        assertMatch(service.getBetweenInclusive(checkDate, checkDate, USER_ID), meal5User, meal4User);
    }

    @Test
    public void getBetweenInclusiveThreeMealsResult() {
        assertMatch(service.getBetweenInclusive(checkDate.minusDays(1), checkDate.minusDays(1), USER_ID),
                meal3User, meal2User, meal1User);
    }

    @Test
    public void getBetweenInclusiveFiveMealsResult() {
        assertMatch(service.getBetweenInclusive(null, null, USER_ID),
                meal5User, meal4User, meal3User, meal2User, meal1User);
    }

    @Test
    public void getAll() {
        List<Meal> expectedList = Arrays.asList(meal5User, meal4User, meal3User, meal2User, meal1User);
        assertEquals(expectedList, service.getAll(USER_ID));
    }

    @Test
    public void updateExist() {
        service.update(getUpdated(), USER_ID);
        assertMatch(service.get(ID_MEAL3_OF_USER, USER_ID), getUpdated());
    }

    @Test
    public void updateNotExist() {
        assertThrows(NotFoundException.class, () -> service.update(notAddedMeal, USER_ID));
    }

    @Test
    public void updateMealOfOtherUser() {
        assertThrows(NotFoundException.class, () -> service.update(meal1Admin, USER_ID));
    }

    @Test
    public void createCorrect() {
        service.create(getNew(), USER_ID);
        assertMatchIgnoreId(service.get(TO_ADD_MEAL_ID, USER_ID), getNew());
    }

    @Test
    public void createDuplicateDate() {
        final Meal duplicatedDateMeal = MealTestData.getNew();
        duplicatedDateMeal.setDateTime(meal2Admin.getDateTime());
        assertThrows(DataAccessException.class, () -> service.create(duplicatedDateMeal, ADMIN_ID));
    }

    @Test
    public void createMealForOtherUser() {
        assertNull(service.create(meal1User, ADMIN_ID));
    }
}