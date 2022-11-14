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
        assertMatch(service.get(ID_MEAL1_OF_USER, USER_ID), meal1User);
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
        assertEquals(0, service.getBetweenInclusive(checkDate, checkDate, USER_ID).size());
    }

    @Test
    public void getBetweenInclusiveThreeDaysResult() {
        assertEquals(3, service.getBetweenInclusive(checkDate, checkDate, ADMIN_ID).size());
    }

    @Test
    public void getAll() {
        List<Meal> expectedList = Arrays.asList(meal3User, meal2User, meal1User);
        assertEquals(expectedList, service.getAll(USER_ID));
    }

    @Test
    public void updateExist() {
        service.update(updateMealOfUser, USER_ID);
        assertMatch(service.get(ID_MEAL3_OF_USER, USER_ID), updateMealOfUser);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExist() {
        service.update(notAddedMeal, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateMealOfOtherUser() {
        service.update(meal1Admin, USER_ID);
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
        duplicatedDateMeal.setDateTime(LocalDateTime.of(checkDate, LocalTime.of(14,0)));
        service.create(duplicatedDateMeal, ADMIN_ID);
    }

    @Test
    public void createMealForOtherUser() {
        assertNull(service.create(meal1User, ADMIN_ID));
    }
}