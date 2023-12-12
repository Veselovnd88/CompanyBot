package ru.veselov.companybot.bot.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class TelegramFacadeUpdateHandlerTest {

    @Mock
    CommandUpdateHandler commandUpdateHandler;

    @Mock
    ChannelConnectUpdateHandler channelConnectUpdateHandler;

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    CallbackQueryDataHandlerContext callbackQueryDataHandlerContext;

    @Mock
    BotStateHandlerContext botStateHandlerContext;

    @InjectMocks
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    Update update;
    CallbackQuery callbackQuery;
    Message message;
    User user;

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        user = Mockito.spy(User.class);
    }

    @Test
    void shouldCallChannelConnectUpdateHandler() {
        ReflectionTestUtils
                .setField(telegramFacadeUpdateHandler, "adminId", TestUtils.ADMIN_ID.toString(), String.class);
        Update updateWithConnectionBotToChannelByAdmin = TestUpdates.getUpdateWithConnectionBotToChannelByAdmin();

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
        Assertions.assertThat(sendMessage.getChatId()).isEqualTo(TestUpdates.getSimpleUser().getId().toString());
        Mockito.verifyNoInteractions(channelConnectUpdateHandler);
    }

    @Test
    void shouldCallCommandUpdateHandlerIfMessageHasCommandEntity() {
        Update updateWithMessageWithCommandByUser = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);

        telegramFacadeUpdateHandler.processUpdate(updateWithMessageWithCommandByUser);

        Mockito.verify(commandUpdateHandler).processUpdate(updateWithMessageWithCommandByUser);
    }


}