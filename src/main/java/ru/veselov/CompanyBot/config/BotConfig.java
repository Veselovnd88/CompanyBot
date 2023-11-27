package ru.veselov.CompanyBot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
@Configuration
@Slf4j
public class BotConfig {
    @Value("${bot.webhookpath}")
    private String webHookPath;

    @Bean
    public TelegramBotsApi telegramBotsApi(){
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            log.info("Api is created");
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    SetWebhook setWebhookInstance(){
        return SetWebhook.builder().url(webHookPath).build();
    }


}
