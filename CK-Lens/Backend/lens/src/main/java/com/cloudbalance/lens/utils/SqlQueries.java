package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.dto.costexplorer.ColumnFilterDTO;
import com.cloudbalance.lens.entity.ColumnName;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PropertySource(value = "classpath:queries.yml", factory = YamlPropertySourceFactory.class)
@Component
public class SqlQueries {

    @Value("${queries.getFilterValue}")
    private String getFilterValueQuery;

    @Value("${queries.getColumnNames}")
    private String getColumnNamesQuery;

    @Value("${queries.databaseName}")
    private String databaseName;

    @Value("${queries.tableName}")
    private String tableName;

    @Value("${queries.schemaName}")
    private String schemaName;

    @Value("${queries.limit}")
    private int limit;


    /**
     * Fetch value of any select filter
     **/

    public String getFilterValue(String columnName) {
        return String.format(getFilterValueQuery, columnName, columnName);
    }

    /**
     * Fetch column names from the table
     **/

    public String getColumnNames() {
        return String.format(getColumnNamesQuery, databaseName, tableName, schemaName);
    }

    /**
     * Dynamic query builder for snowflake queries
     **/

    public String builder(ColumnNameRepository columnNameRepository,
                          String linkedId, String groupBy,
                          String startDate, String endDate,
                          ColumnFilterDTO filter) {

        StringBuilder query = new StringBuilder();

        query.append("SELECT TO_CHAR(USAGESTARTDATE, 'YYYY-MM') AS USAGE_DATE, ")
                .append(groupBy)
                .append(", ")
                .append("SUM(LINEITEM_UNBLENDEDCOST) AS TOTAL_USAGE_COST ")
                .append("FROM ").append(tableName)
                .append(" WHERE ");

        query.append("USAGESTARTDATE BETWEEN TO_DATE('")
                .append(startDate)
                .append("') AND TO_DATE('")
                .append(endDate)
                .append("') ");

        query.append("AND LINKEDACCOUNTID = '")
                .append(linkedId.replace("'", "''"))
                .append("' ");

        if (filter != null && filter.getFilters() != null) {
            for (ColumnFilterDTO.ColumnFilter columnFilter : filter.getFilters()) {
                String column = columnFilter.getColumnName();
                List<String> values = columnFilter.getFilterValues();

                if (column != null && values != null && !values.isEmpty()) {
                    String inClause = values.stream()
                            .map(val -> "'" + val.replace("'", "''") + "'")
                            .collect(Collectors.joining(", "));
                    query.append("AND ").append(getColumnNameFromFieldName(columnNameRepository, column))
                            .append(" IN (")
                            .append(inClause)
                            .append(") ");
                }
            }
        }

        query.append("GROUP BY TO_CHAR(USAGESTARTDATE, 'YYYY-MM'), ")
                .append(groupBy)
                .append(" ")
                .append("ORDER BY USAGE_DATE, TOTAL_USAGE_COST DESC ")
                .append("LIMIT ")
                .append(limit);

        return query.toString();
    }

    /**
     * Fetch and validate columnName by fieldName from Entity ColumnName
     **/

    public String getColumnNameFromFieldName(ColumnNameRepository columnNameRepository, String fieldName) {
        ColumnName columnNameEntity = columnNameRepository.findByFieldName(fieldName)
                .orElseThrow(() -> new ResourceNotFoundException("No column found for field name: " + fieldName));
        return columnNameEntity.getNameOfColumn();
    }


}
