package com.example.pj.mb;

import com.example.pj.dto.Schema;
import com.example.pj.dto.Table;
import com.example.pj.dto.TableField;
import com.example.pj.services.DataService;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@ManagedBean
@RequestScoped
public class ServerBean implements Serializable {
    public static <T, A> Collector<T, ?, Map<A, List<T>>> groupingByWithNullKeys(Function<? super T, ? extends A> classifier) {
        return Collectors.toMap(
                classifier,
                Collections::singletonList,
                (List<T> oldList, List<T> newEl) -> {
                    List<T> newList = new ArrayList<>(oldList.size() + 1);
                    newList.addAll(oldList);
                    newList.addAll(newEl);
                    return newList;
                }
        );
    }

    // *****************************************************************************************************************


    @Autowired
    private DataService dataService;

    @Getter
    @Setter
    private Schema selectedSchema;

    @Getter
    @Setter
    private Table selectedTable;

    @Getter
    @Setter
    private String sql;

    @Getter
    private List<Map<String, Object>> sqlResult = new ArrayList<>();

    // *****************************************************************************************************************
    public void selectSchema(final Schema schema) {
        selectedSchema = schema;
    }

    public void selectTable(final Table table) {
        selectedTable = table;

        StringBuffer buffer = new StringBuffer("SELECT * FROM ");
        if (null != selectedSchema && !StringUtils.isEmpty(selectedSchema.getName())) {
            buffer.append(selectedSchema.getName());
            buffer.append('.');
        }

        buffer.append(selectedTable.getName());

        sql = buffer.toString();
    }

    public List<Schema> schemas() throws SQLException {
        Connection connection = dataService.getConnection();
        if (null == connection) {
            return Collections.EMPTY_LIST;
        }

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from SYSTEM.CATALOG");

        // ResultSetMetaData metaData = resultSet.getMetaData();

        List<TableField> tableFields = new ArrayList<>();
        while (resultSet.next()) {
            tableFields.add(
                    TableField.builder()
                            .schema(resultSet.getString("\"TABLE_SCHEM\""))
                            .table(resultSet.getString("\"TABLE_NAME\""))
                            .column(resultSet.getString("\"COLUMN_NAME\""))
                            .build()
            );
        }

        List<Schema> schemas = new ArrayList<>();
        Map<String, List<TableField>> schemaFields = tableFields.stream().collect(groupingByWithNullKeys(TableField::getSchema));
        for (Map.Entry<String, List<TableField>> schemaEntry : schemaFields.entrySet()) {
            Map<String, List<TableField>> tableMap = schemaEntry.getValue().stream().collect(Collectors.groupingBy(TableField::getTable));

            List<Table> tables = tableMap.entrySet()
                    .stream()
                    .map(tableEntry -> Table.builder()
                            .schema(schemaEntry.getKey())
                            .name(tableEntry.getKey())
                            .fields(tableEntry.getValue())
                            .build())
                    .sorted(Comparator.comparing(Table::getName))
                    .collect(Collectors.toList());

            schemas.add(Schema.builder()
                    .name(schemaEntry.getKey())
                    .tables(tables)
                    .build());
        }

        Collections.sort(schemas, Comparator.comparing(Schema::getViewName));

        return schemas;
    }

    public List<Table> tables() {
        if (null == selectedSchema)
            return Collections.EMPTY_LIST;

        return selectedSchema.getTables();
    }

    public void executeSql() {
        /*
        DataTable table = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":sqlForm:queryTable");
        table.resetColumns();
        */

        Connection connection = dataService.getConnection();
        if (null == connection) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Connection Error", "No connection, need reconnect"));
            return;
        }

        try (Statement statement = connection.createStatement()) {
            sqlResult.clear();
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();

                List<String> cols = new ArrayList<>();
                for (int index = 1; index <= metaData.getColumnCount(); index++)
                    cols.add(metaData.getColumnName(index));

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (String colName : cols) {
                        Object val = resultSet.getObject(colName);
                        row.put(colName, val);
                    }
                    sqlResult.add(row);
                }
            }
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", e.getMessage()));
            return;
        }

    }

    public List<String> getTableColumns() {
        if (null == selectedTable)
            return Collections.EMPTY_LIST;

        return selectedTable.getFields()
                .stream()
                .map(TableField::getColumn)
                .collect(Collectors.toList());
    }

}
