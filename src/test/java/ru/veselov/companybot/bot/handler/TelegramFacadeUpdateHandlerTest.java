package ru.veselov.companybot.bot.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.handler.callback.CallbackQueryUpdateHandler;
import ru.veselov.companybot.bot.handler.message.MessageUpdateHandler;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class TelegramFacadeUpdateHandlerTest {

    @Mock
    ChannelConnectUpdateHandler channelConnectUpdateHandler;

    @Mock
    CallbackQueryUpdateHandler callbackQueryUpdateHandler;

    @Mock
    MessageUpdateHandler messageUpdateHandler;

    @InjectMocks
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @Test
    void shouldCallChannelConnectUpdateHandler() {
        ReflectionTestUtils
                .setField(telegramFacadeUpdateHandler, "adminId", TestUtils.ADMIN_ID.toString(), String.class);
        Update updateWithConnectionBotToChannelByAdmin = TestUpdates.getUpdateWithConnectionToChannelByAdmin();

        telegramFacadeUpdateHandler.processUpdate(updateWithConnectionBotToChannelByAdmin);

        Mockito.verify(channelConnectUpdateHandler).processUpdate(updateWithConnectionBotToChannelByAdmin);
    }

    @Test
    void shouldNotCallChannelConnectUpdateHandlerIfUserNotAdmin() {
        ReflectionTestUtils
                .setField(telegramFacadeUpdateHandler, "adminId", TestUtils.ADMIN_ID.toString(), String.class);
        Update updateWithConnectionBotToChannelByUser = TestUpdates.getUpdateWithConnectionBotToChannelByUser();

        SendMessage sendMessage = (SendMessage) telegramFacadeUpdateHandler
                .processUpdate(updateWithConnectionBotToChannelByUser);

        Assertions.assertThat(sendMessage.getText()).startsWith("Я работаю только");
        Assertions.assertThat(sendMessage.getChatId()).isEqualTo(TestUtils.getSimpleUser().getId().toString());
        Mockito.verifyNoInteractions(channelConnectUpdateHandler);
    }

    @Test
    void shouldCallMessageUpdateHandlerIfUpdateContainsMessage() {
        Update updateWithMessageWithCommandByUser = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);

        telegramFacadeUpdateHandler.processUpdate(updateWithMessageWithCommandByUser);

        Mockito.verify(messageUpdateHandler).processUpdate(updateWithMessageWithCommandByUser);
    }

    @Test
    void shouldCallCallbackUpdateHandlerIfUpdateContainsCallback() {
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("anyCallback");

        telegramFacadeUpdateHandler.processUpdate(update);

        Mockito.verify(callbackQueryUpdateHandler).processUpdate(update);
    }

    @Test
    void shouldReturnNullIfNotSupportedUpdateReceived() {
        Update update = new Update();

        BotApiMethod<?> botApiMethod = telegramFacadeUpdateHandler.processUpdate(update);

        Assertions.assertThat(botApiMethod).isNull();
        Mockito.verifyNoInteractions(callbackQueryUpdateHandler, messageUpdateHandler, channelConnectUpdateHandler);
    }

}
