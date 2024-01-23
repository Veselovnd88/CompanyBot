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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotProperties;
import ru.veselov.companybot.bot.handler.impl.ChannelConnectUpdateHandlerImpl;
import ru.veselov.companybot.service.ChatService;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class ChannelConnectUpdateHandlerImplTest {

    @Mock
    ChatService chatService;

    @InjectMocks
    ChannelConnectUpdateHandlerImpl channelConnectUpdateHandler;

    User user;

    User botUser;

    @BeforeEach
    void init() {
        BotProperties botProperties = new BotProperties();
        botProperties.setBotId(TestUtils.BOT_ID);
        ReflectionTestUtils.setField(channelConnectUpdateHandler, "botProperties",
                botProperties, BotProperties.class);
        user = TestUtils.getSimpleUser();
        botUser = TestUtils.getBotUser();

    }

    @Test
    void shouldSaveChatWhenBotIsConnectedToChannel() {
        botUser.setId(TestUtils.BOT_ID);
        Update update = TestUpdates.getUpdateWithConnectionBotWithAdministratorStatusToChannelByAdmin();
        Chat chat = update.getMyChatMember().getChat();

        SendMessage sendMessage = channelConnectUpdateHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getChatId()).isEqualTo(TestUtils.ADMIN_ID.toString());
        Mockito.verify(chatService).save(chat);
    }

    @Test
    void shouldRemoveChatWhenBotIsRemovedFromChannel() {
        Update update = TestUpdates.getUpdateWithConnectionBotWithLeftStatusToChannelByAdmin();
        Chat chat = update.getMyChatMember().getChat();

        channelConnectUpdateHandler.processUpdate(update);

        Mockito.verify(chatService).remove(chat.getId());
    }

    @Test
    void shouldRemoveChatIfBotWasKickedFromChat() {
        Update update = TestUpdates.getUpdateWithConnectionBotWithKickedStatusToChannelByAdmin();
        Chat chat = update.getMyChatMember().getChat();

        channelConnectUpdateHandler.processUpdate(update);

        Mockito.verify(chatService).remove(chat.getId());
    }

    @Test
    void shouldReturnNullIfUnsupportedCommandOccurred() {
        Update update = TestUpdates.getUpdateWithConnectionBotWithUnsupportedStatusToChannelByAdmin();

        SendMessage sendMessage = channelConnectUpdateHandler.processUpdate(update);

        Assertions.assertThat(sendMessage).isNull();
    }

    @Test
    void shouldThrowExceptionIfNotBotIdWasHandled() {
        Update update = TestUpdates.getUpdateWithConnectionNoBotWithUnsupportedStatusToChannelByAdmin();

        SendMessage sendMessage = channelConnectUpdateHandler.processUpdate(update);

        Assertions.assertThat(sendMessage).isNull();
    }

}
