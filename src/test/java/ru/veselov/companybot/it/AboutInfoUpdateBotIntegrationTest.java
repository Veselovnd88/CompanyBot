package ru.veselov.companybot.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.EnableTestContainers;
import ru.veselov.companybot.entity.CompanyInfoEntity;
import ru.veselov.companybot.repository.CompanyInfoRepository;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
@EnableTestContainers
class AboutInfoUpdateBotIntegrationTest {

    @MockBean
    CompanyBot bot;

    @MockBean
    BotConfig botConfig;

    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @Autowired
    CompanyInfoRepository companyInfoRepository;

    @Autowired
    UserStateCache userStateCache;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("bot.adminId", () -> TestUtils.ADMIN_ID);
    }

    @AfterEach
    void clear() {
        companyInfoRepository.deleteAll();
        userStateCache.reset();
    }

    @Test
    void processUpdate_ifCorrectFlow_updateInfo() {
        Update start = TestUpdates.getUpdateWithMessageWithCommandByAdmin(BotCommands.START);
        telegramFacadeUpdateHandler.processUpdate(start);
        Update updatePressCommand = TestUpdates.getUpdateWithMessageWithCommandByAdmin(BotCommands.UPDATE_INFO);
        BotApiMethod<?> commandMessage = telegramFacadeUpdateHandler.processUpdate(updatePressCommand);
        SendMessage commandSendMessage = TestUtils.checkSendMessageInstanceAndCast(commandMessage);
        Assertions.assertThat(commandSendMessage.getText()).as("Check answer message")
                .isEqualTo(MessageUtils.AWAIT_INFO_MESSAGE);
        Update updateInfo = TestUpdates.getUpdateWithMessageWithTextContentByAdmin();
        BotApiMethod<?> resultMessage = telegramFacadeUpdateHandler.processUpdate(updateInfo);
        SendMessage resultSendMessage = TestUtils.checkSendMessageInstanceAndCast(resultMessage);
        Assertions.assertThat(resultSendMessage.getText()).isEqualTo(MessageUtils.NEW_INFO_MSG);

        List<CompanyInfoEntity> last = companyInfoRepository.findLast();
        Assertions.assertThat(last).as("Check if list with info is not empty")
                .isNotEmpty();
        Assertions.assertThat(last.get(0).getInfo().getText()).as("Check of first element is our new info message")
                .startsWith(TestUpdates.STUB_TEXT);
    }

    @Test
    void processUpdate_ifNotAdmin_ShouldReturnSendMessageWithErrorAnswer() {
        Update start = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.START);
        telegramFacadeUpdateHandler.processUpdate(start);

        Update updatePressCommand = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.UPDATE_INFO);
        BotApiMethod<?> commandMessage = telegramFacadeUpdateHandler.processUpdate(updatePressCommand);
        SendMessage commandSendMessage = TestUtils.checkSendMessageInstanceAndCast(commandMessage);
        Assertions.assertThat(commandSendMessage.getText()).isEqualTo(MessageUtils.ANOTHER_ACTION_NO_ADMIN);
    }

    @Test
    void processUpdate_ifWrongState_ShouldReturnSendMessageWithErrorAnswer() {
        Update updatePressCommand = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.UPDATE_INFO);
        BotApiMethod<?> commandMessage = telegramFacadeUpdateHandler.processUpdate(updatePressCommand);
        SendMessage commandSendMessage = TestUtils.checkSendMessageInstanceAndCast(commandMessage);
        Assertions.assertThat(commandSendMessage.getText()).isEqualTo(MessageUtils.ANOTHER_ACTION_NO_ADMIN);
    }

    @Test
    void processUpdate_ifVeryLongMessage_ShouldReturnSendMessageWithErrorAnswer() {
        Update start = TestUpdates.getUpdateWithMessageWithCommandByAdmin(BotCommands.START);
        telegramFacadeUpdateHandler.processUpdate(start);
        Update updatePressCommand = TestUpdates.getUpdateWithMessageWithCommandByAdmin(BotCommands.UPDATE_INFO);
        telegramFacadeUpdateHandler.processUpdate(updatePressCommand);
        Update updateInfo = TestUpdates.getUpdateWithMessageWithTextContentByAdmin();
        updateInfo.getMessage().setText("a".repeat(901));
        BotApiMethod<?> resultMessage = telegramFacadeUpdateHandler.processUpdate(updateInfo);
        SendMessage resultSendMessage = TestUtils.checkSendMessageInstanceAndCast(resultMessage);
        Assertions.assertThat(resultSendMessage.getText()).as("Check error message text")
                .isEqualTo(MessageUtils.INFO_MSG_IS_TOO_LONG);
    }

}
