package ru.veselov.companybot.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 * Configuration class for managing test containers, it this situation we don't need use
 * {@link org.testcontainers.junit.jupiter.Testcontainers } annotation
 * on class, and {@link org.testcontainers.junit.jupiter.Container} annotation on the container itself.
 * In this class we will start containers that will be available for all our tests
 */
public class PostgresTestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("cbotDB");

    static {
        postgresContainer.start();
    }


    //initialize environment before dynamic property source applied
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
                "spring.liquibase.enabled=true",
                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword()).applyTo(ctx.getEnvironment());
    }

}
