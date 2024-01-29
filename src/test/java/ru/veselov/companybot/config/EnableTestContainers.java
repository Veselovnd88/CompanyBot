package ru.veselov.companybot.config;

import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for including test containers for integration test
 *
 * @see PostgresTestContainersInitializer
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = PostgresTestContainersInitializer.class)
public @interface EnableTestContainers {
}