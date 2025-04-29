package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.dto.costexplorer.ColumnFilterDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSetMetaData;
import java.util.List;

@Repository
@Slf4j
public class SnowflakeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ColumnNameRepository columnNameRepository;
    private final SqlQueries sqlQueries;

    public SnowflakeRepository(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate jdbcTemplate,
                               ColumnNameRepository columnNameRepository,
                               SqlQueries sqlQueries) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnNameRepository = columnNameRepository;
        this.sqlQueries = sqlQueries;
    }


    public List<CostExplorerResponseDTO.CostExplorerData> getData(String linkedId, String groupBy, String startDate,
                                                                  String endDate, ColumnFilterDTO filterDTO) {
        String query = sqlQueries.builder(columnNameRepository, linkedId, groupBy, startDate, endDate, filterDTO);

        log.info("\nGenerated SQL Query: {}\n", query);

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String columnName = null;
            Double cost = null;
            String date = null;
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                if (groupBy.equalsIgnoreCase(columnLabel)) {
                    columnName = rs.getString(i);
                } else if ("TOTAL_USAGE_COST".equalsIgnoreCase(columnLabel)) {
                    cost = rs.getDouble(i);
                } else if ("USAGE_DATE".equalsIgnoreCase(columnLabel)) {
                    date = rs.getString(i);
                }
            }
            return new CostExplorerResponseDTO.CostExplorerData(columnName, cost, date);
        });
    }

    public List<String> getFilter(String columnName) {
        return jdbcTemplate.queryForList(sqlQueries.getFilterValue(columnName), String.class);
    }

}
