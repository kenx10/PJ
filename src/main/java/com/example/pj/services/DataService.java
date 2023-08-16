package com.example.pj.services;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DataService {
    private final Map<String, Connection> sessionMap = new HashMap<>();

    public void putConnection(String sessionId, Connection connection) {
        sessionMap.put(sessionId, connection);
    }

    public void putConnection(Connection connection) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        sessionMap.put(session.getId(), connection);
    }

    public Connection getConnection(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public Connection getConnection() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (null == session)
            return null;

        return getConnection(session.getId());
    }

    public void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.warn("Connection", e);
            }
        }
    }
}
