package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;

public class MealTestData {
    public static final int ID_MEAL1_OF_USER = GUEST_ID + 1;
    public static final int ID_MEAL2_OF_USER = ID_MEAL1_OF_USER + 1;
    public static final int ID_MEAL3_OF_USER = ID_MEAL2_OF_USER + 1;
    public static final int ID_MEAL4_OF_USER = ID_MEAL3_OF_USER + 1;
    public static final int ID_MEAL5_OF_USER = ID_MEAL4_OF_USER + 1;
    public static final int ID_MEAL1_OF_ADMIN = ID_MEAL5_OF_USER + 1;
    public static final int ID_MEAL2_OF_ADMIN = ID_MEAL1_OF_ADMIN + 1;
    public static final int ID_MEAL3_OF_ADMIN = ID_MEAL2_OF_ADMIN + 1;
    public static final int NOT_FOUND_MEAL_ID = ID_MEAL3_OF_ADMIN + 1;
    public static final LocalDate CHECK_DATE = LocalDate.of(2020, 1, 31);

    public static final Meal MEAL1_OF_USER = new Meal(ID_MEAL1_OF_USER, LocalDateTime.of(CHECK_DATE.minusDays(1),
            LocalTime.of(10, 0, 0)), "Завтрак", 1000);
    public static final Meal MEAL2_OF_USER = new Meal(ID_MEAL2_OF_USER, LocalDateTime.of(CHECK_DATE.minusDays(1),
            LocalTime.of(14, 0, 0)), "Обед", 1500);
    public static final Meal MEAL3_OF_USER = new Meal(ID_MEAL3_OF_USER, LocalDateTime.of(CHECK_DATE.minusDays(1),
            LocalTime.of(23, 59, 59)), "Ужин", 501);
    public static final Meal MEAL4_OF_USER = new Meal(ID_MEAL4_OF_USER, LocalDateTime.of(CHECK_DATE,
            LocalTime.of(11, 30, 0)), "Завтрак", 1000);
    public static final Meal MEAL5_OF_USER = new Meal(ID_MEAL5_OF_USER, LocalDateTime.of(CHECK_DATE,
            LocalTime.of(19, 40, 0)), "Ужин", 450);
    public static final Meal UPDATED_MEAL_OF_USER = new Meal(ID_MEAL3_OF_USER, LocalDateTime.of(CHECK_DATE.minusDays(1),
            LocalTime.of(20, 0, 0)), "Ужин NEW!!!", 500);

    public static final Meal MEAL1_ADMIN = new Meal(ID_MEAL1_OF_ADMIN, LocalDateTime.of(CHECK_DATE,
            LocalTime.of(10, 0, 0)), "Завтрак", 500);
    public static final Meal MEAL2_ADMIN = new Meal(ID_MEAL2_OF_ADMIN, LocalDateTime.of(CHECK_DATE,
            LocalTime.of(14, 0, 0)), "Обед", 1500);
    public static final Meal MEAL3_ADMIN = new Meal(ID_MEAL3_OF_ADMIN, LocalDateTime.of(CHECK_DATE,
            LocalTime.of(19, 0, 0)), "Ужин", 450);

    public static final Meal NOT_ADDED_MEAL = new Meal(NOT_FOUND_MEAL_ID, LocalDateTime.of(CHECK_DATE.minusDays(60),
            LocalTime.of(0, 0, 0)), "FAKE MEAL!!!", 666);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2020, 1, 29, 22, 0, 0), "New Meal", 999);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
