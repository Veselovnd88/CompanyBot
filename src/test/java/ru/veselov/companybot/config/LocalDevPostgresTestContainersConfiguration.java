package ru.veselov.companybot.config;

import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class LocalDevPostgresTestContainersConfiguration {

    @Bean
    @ServiceConnection(type = JdbcConnectionDetails.class)
    public PostgreSQLContainer<?> postgresContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.enabled", () -> true);
        return new PostgreSQLContainer<>("postgres:16").withDatabaseName("cbotDB");
    }

}
