package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;

@ActiveProfiles(Profiles.HSQL_DB)
public class HsqldbJdbcMealServiceTest extends JdbcMealServiceTest {
}
