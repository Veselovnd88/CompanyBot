package ru.veselov.companybot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.exception.CriticalBotException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotConfig {

    private final CompanyBot companyBot;

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            log.info("Telegram api is created");
            telegramBotsApi.registerBot(companyBot);
            log.info("Company bot registered");
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            log.error("Something went wrong: [{}]", e.getMessage());
            throw new CriticalBotException(e.getMessage(), e);
        }
    }

}
