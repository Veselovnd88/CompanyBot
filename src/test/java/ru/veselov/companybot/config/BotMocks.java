package ru.veselov.companybot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.companybot.bot.CompanyBot;

@TestConfiguration
public class BotMocks {

    @MockBean
    CompanyBot companyBot;

    @MockBean
    BotConfig botConfig;
}
