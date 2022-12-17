package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;

public class JdbcMealPostgresRepository extends ProfiledJdbcMealRepository<LocalDateTime> {
    public JdbcMealPostgresRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected LocalDateTime getDateType(LocalDateTime localDateTime) {
        return localDateTime;
    }
}
