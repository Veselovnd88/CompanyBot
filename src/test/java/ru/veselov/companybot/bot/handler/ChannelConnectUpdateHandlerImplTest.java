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
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.companybot.bot.BotConstant;
import ru.veselov.companybot.bot.BotProperties;
import ru.veselov.companybot.bot.handler.impl.ChannelConnectUpdateHandlerImpl;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.ChatService;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class ChannelConnectUpdateHandlerImplTest {

    @Mock
    ChatService chatService;

    @InjectMocks
    ChannelConnectUpdateHandlerImpl channelConnectUpdateHandler;

    Update update;

    User user;

    User botUser;

    ChatMemberUpdated chatMemberUpdated;

    ChatMember chatMember;

    Chat chat;

    @BeforeEach
    void init() {
        BotProperties botProperties = new BotProperties();
        botProperties.setBotId(TestUtils.botId);
        ReflectionTestUtils.setField(channelConnectUpdateHandler, "botProperties",
                botProperties, BotProperties.class);
        update = Mockito.spy(Update.class);
        user = Mockito.spy(User.class);
        user.setId(100L);
        botUser = Mockito.spy(User.class);
        chatMemberUpdated = Mockito.spy(ChatMemberUpdated.class);
        chatMember = Mockito.spy(ChatMember.class);
        chat = Mockito.spy(Chat.class);
        chat.setId(-100L);
        update.setMyChatMember(chatMemberUpdated);
        chatMemberUpdated.setFrom(user);
        chatMemberUpdated.setNewChatMember(chatMember);
        Mockito.when(chatMemberUpdated.getNewChatMember()).thenReturn(chatMember);
        Mockito.when(chatMember.getUser()).thenReturn(botUser);
        chatMemberUpdated.setChat(chat);
    }

    @Test
    void shouldSaveChatWhenBotIsConnectedToChannel() {
        botUser.setId(TestUtils.botId);
        Mockito.when(chatMember.getStatus()).thenReturn(BotConstant.ADMINISTRATOR);
        SendMessage sendMessage = channelConnectUpdateHandler.processUpdate(update);
        Assertions.assertThat(sendMessage.getChatId()).isEqualTo(user.getId().toString());
        Mockito.verify(chatService).save(chat);
    }

    @Test
    void shouldRemoveChatWhenBotIsRemovedFromChannel() {
        botUser.setId(TestUtils.botId);
        Mockito.when(chatMember.getStatus()).thenReturn(BotConstant.LEFT);
        channelConnectUpdateHandler.processUpdate(update);
        Mockito.verify(chatService).remove(chat.getId());
    }

    @Test
    void shouldRemoveChatIfBotWasKickedFromChat() {
        botUser.setId(TestUtils.botId);
        Mockito.when(chatMember.getStatus()).thenReturn(BotConstant.KICKED);
        channelConnectUpdateHandler.processUpdate(update);
        Mockito.verify(chatService).remove(chat.getId());
    }

    @Test
    void shouldThrowExceptionIfUnavailableCommandOccurred() {
        botUser.setId(TestUtils.botId);
        Mockito.when(chatMember.getStatus()).thenReturn("unknown");
        Assertions.assertThatThrownBy(
                () -> channelConnectUpdateHandler.processUpdate(update)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @Test
    void shouldThrowExceptionIfNotBotIdWasHandled() {
        botUser.setId(9L);
        Assertions.assertThatThrownBy(
                () -> channelConnectUpdateHandler.processUpdate(update)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

}
