package com.example.pj.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class C3p0DataSource {
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();

    static {
        Properties properties = new Properties();
        properties.setProperty("phoenix.schema.mapSystemTablesToNamespace", "true");
        properties.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");

        try {
            cpds.setDriverClass("org.h2.Driver");
            cpds.setJdbcUrl("jdbc:phoenix:localhost");
            cpds.setProperties(properties);
        } catch (PropertyVetoException e) {
            log.warn(Marker.ANY_MARKER, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    public static DataSource getDataSource() {
        return cpds;
    }
}
