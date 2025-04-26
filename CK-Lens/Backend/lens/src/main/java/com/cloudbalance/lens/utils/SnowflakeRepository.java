package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.dto.costexplorer.ColumnFilterDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSetMetaData;
import java.util.List;

@Repository
@Slf4j
public class SnowflakeRepository {

    /**
     * private final JdbcTemplate jdbcTemplate;
     * public SnowflakeRepository(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate jdbcTemplate) {
     * this.jdbcTemplate = jdbcTemplate;
     * }
     **/

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ColumnNameRepository columnNameRepository;
    @Autowired
    private SqlQueries sqlQueries;

    public List<CostExplorerResponseDTO.CostExplorerData> getData(String linkedId, String groupBy, String startDate, String endDate, ColumnFilterDTO filterDTO) {
        String query = sqlQueries.builder(columnNameRepository,linkedId, groupBy, startDate, endDate, filterDTO);
        log.info("Generated SQL Query: {}", query);
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String columnName = null;
            Double cost = null;
            String date = null;
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                log.info("Column {}: {}", columnLabel, rs.getString(i));
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
