package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;

@ActiveProfiles(Profiles.JDBC_AUTO_CHOOSE_DB)
public class AutoChooseDbJdbcMealServiceTest extends JdbcMealServiceTest {
}
