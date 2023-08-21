package com.example.pj.dao;

import com.example.pj.domain.CommonEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class CommonEventRowMapper implements RowMapper<CommonEvent> {
    @Override
    public CommonEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CommonEvent.builder()
                .id(rs.getLong(rowNum))
                .instant(Instant.ofEpochSecond(rs.getLong(rowNum)))
                .data(rs.getString(rowNum))
                .build();
    }
}
