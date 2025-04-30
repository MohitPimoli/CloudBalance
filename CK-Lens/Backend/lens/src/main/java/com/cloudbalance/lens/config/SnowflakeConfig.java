package com.cloudbalance.lens.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class SnowflakeConfig {
    private final String url = System.getenv("SNOWFLAKE_URL");
    private final String username = System.getenv("SNOWFLAKE_USERNAME");
    private final String password = System.getenv("SNOWFLAKE_PASSWORD");
    private final String warehouse = System.getenv("SNOWFLAKE_WAREHOUSE");

    @Bean(name = "snowflakeDataSource")
    public DataSource snowflakeDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        String connectionUrl = url;
        if (warehouse != null && !warehouse.isEmpty()) {
            if (connectionUrl.contains("?")) {
                connectionUrl += "&warehouse=" + warehouse;
            } else {
                connectionUrl += "?warehouse=" + warehouse;
            }
        }
        dataSource.setJdbcUrl(connectionUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        return dataSource;
    }

    @Bean(name = "snowflakeJdbcTemplate")
    public JdbcTemplate snowflakeJdbcTemplate(@Qualifier("snowflakeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
