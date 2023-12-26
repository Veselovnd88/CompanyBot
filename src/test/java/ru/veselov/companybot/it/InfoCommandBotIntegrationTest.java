package ru.veselov.companybot.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.UserActionsUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InfoCommandBotIntegrationTest extends PostgresTestContainersConfiguration {

    @MockBean
    CompanyBot bot;

    @MockBean
    BotConfig botConfig;

    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @Test
    void shouldReturnSendMessageWithInfo() {
        Update update = UserActionsUtils.userPressInfoButton();
        BotApiMethod<?> infoAnswer = telegramFacadeUpdateHandler.processUpdate(update);

        Assertions.assertThat(infoAnswer).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) infoAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.INFO);
    }
}
