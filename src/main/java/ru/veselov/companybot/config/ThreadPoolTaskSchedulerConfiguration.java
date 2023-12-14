package ru.veselov.companybot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@Slf4j
public class ThreadPoolTaskSchedulerConfiguration {

    @Bean
    @Qualifier("senderTaskExecutor")
    public ThreadPoolTaskScheduler threadPoolSenderTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "Task executor");
        return threadPoolTaskScheduler;
    }

    @Bean
    @Qualifier("BotMessageSendExecutor")
    public ThreadPoolTaskScheduler threadPoolBotMessageSenderTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "BotMessage Sender");
        return threadPoolTaskScheduler;
    }

}
