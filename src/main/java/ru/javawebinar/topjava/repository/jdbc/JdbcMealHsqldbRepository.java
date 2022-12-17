package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class JdbcMealHsqldbRepository extends ProfiledJdbcMealRepository<Timestamp> {
    public JdbcMealHsqldbRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected Timestamp getDateType(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
