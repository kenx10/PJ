package com.example.pj.mb;

import com.example.pj.services.DataService;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Getter
@Setter
@ManagedBean
@SessionScoped
public class ConnectionBean implements Serializable {
    static {
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String url = "jdbc:phoenix:localhost"; // jdbc:phoenix:hbase01.codeforensics.ru:/hbase
    private Boolean mapSystemTablesToNamespace = true;
    private Boolean isNamespaceMappingEnabled = false;

    @Autowired
    private DataService dataService;

    public void tryToConnect() {
        Properties properties = new Properties();
        properties.setProperty("phoenix.schema.mapSystemTablesToNamespace", "" + mapSystemTablesToNamespace);
        properties.setProperty("phoenix.schema.isNamespaceMappingEnabled", "" + isNamespaceMappingEnabled);

        try {
            Connection connection = DriverManager.getConnection(url, properties);
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Connected", "connection " + url));
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("server.xhtml");
            dataService.putConnection(connection);
        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Connection Error", e.getMessage()));
        }
    }
}
