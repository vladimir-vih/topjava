package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.sql.DataSource;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = {"classpath:db/initDB.sql"}, config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    private static final Logger log = LoggerFactory.getLogger(MealServiceTest.class);

    @Autowired
    private MealService service;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        log.info("do setUp before test");
        Resource resource = new ClassPathResource("db/populateDB.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
    }

    @Test
    public void getExist() {
        log.info("do getExist");
        assertMatch(service.get(ID_MEAL1_OF_USER, USER_ID), MEAL1_OF_USER);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExist() {
        log.info("do getNotExist");
        service.get(NOT_FOUND_MEAL_ID, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getMealOtherUser() {
        log.info("do getNotExist");
        service.get(ID_MEAL1_OF_ADMIN, USER_ID);
    }

    @Test
    public void deleteExist() {
        log.info("do deleteExist");
        service.delete(ID_MEAL1_OF_USER, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExist() {
        log.info("do deleteNotExist");
        service.delete(NOT_FOUND_MEAL_ID, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteMealOtherUser() {
        log.info("do deleteMealOtherUser");
        service.delete(ID_MEAL1_OF_ADMIN, USER_ID);
    }

    @Test
    public void getBetweenInclusiveEmptyResult() {
        assertEquals(0, service.getBetweenInclusive(CHECK_DATE, CHECK_DATE, USER_ID).size());
    }

    @Test
    public void getBetweenInclusiveThreeDaysResult() {
        assertEquals(3, service.getBetweenInclusive(CHECK_DATE, CHECK_DATE, ADMIN_ID).size());
    }

    @Test
    public void getAll() {
        List<Meal> expectedList = Arrays.asList(MEAL3_OF_USER, MEAL2_OF_USER, MEAL1_OF_USER);
        assertEquals(expectedList, service.getAll(USER_ID));
    }

    @Test
    public void updateExist() {
        service.update(UPDATED_MEAL_OF_USER, USER_ID);
        assertMatch(service.get(ID_MEAL3_OF_USER, USER_ID), UPDATED_MEAL_OF_USER);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExist() {
        service.update(NOT_ADDED_MEAL, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateMealOfOtherUser() {
        service.update(MEAL1_ADMIN, USER_ID);
    }

    @Test
    public void createNotExist() {
        Meal testMeal = MealTestData.getNew();
        Meal createdMeal = service.create(testMeal, USER_ID);
        testMeal.setId(USER_ID);
        assertMatch(createdMeal, testMeal);
    }

    @Test(expected = DataAccessException.class)
    public void createDuplicateDate() {
        final Meal duplicatedDateMeal = MealTestData.getNew();
        duplicatedDateMeal.setDateTime(LocalDateTime.of(CHECK_DATE, LocalTime.of(14,00)));
        service.create(duplicatedDateMeal, ADMIN_ID);
    }

    @Test
    public void createMealForOtherUser() {
        assertNull(service.create(MEAL1_OF_USER, ADMIN_ID));
    }
}