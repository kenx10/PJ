package com.example.pj.dao;

import com.example.pj.domain.CommonEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommonEventDao {
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<CommonEvent> findAll() {
        return jdbcTemplate.query("select * from common_events", new CommonEventRowMapper());
    }
}
