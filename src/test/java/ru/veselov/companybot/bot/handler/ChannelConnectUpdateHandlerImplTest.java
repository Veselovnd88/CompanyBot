package ru.veselov.companybot.bot.handler;


import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.companybot.bot.BotInfo;
import ru.veselov.companybot.bot.handler.impl.ChannelConnectUpdateHandlerImpl;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.ChatService;

@ExtendWith(MockitoExtension.class)
class ChannelConnectUpdateHandlerImplTest {

    @Mock
    ChatService chatService;

    @InjectMocks
    ChannelConnectUpdateHandlerImpl channelConnectUpdateHandler;

    Update update;
    User user;
    User botUser;
    Message message;
    ChatMemberUpdated chatMemberUpdated;
    ChatMember chatMember;
    Chat chat;

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        user = spy(User.class);
        user.setId(100L);
        botUser = spy(User.class);
        message = spy(Message.class);
        chatMemberUpdated = spy(ChatMemberUpdated.class);
        chatMember = spy(ChatMember.class);
        chat = spy(Chat.class);
        chat.setId(-100L);
        update.setMyChatMember(chatMemberUpdated);
        chatMemberUpdated.setFrom(user);
        chatMemberUpdated.setNewChatMember(chatMember);
        when(chatMemberUpdated.getNewChatMember()).thenReturn(chatMember);
        when(chatMember.getUser()).thenReturn(botUser);
        chatMemberUpdated.setChat(chat);

    }

    @Test
    @SneakyThrows
    void connectBotToChannelTest() {
        /*Бот присоединен к каналу админом*/
        botUser.setId(BotInfo.botId);
        when(chatMember.getStatus()).thenReturn("administrator");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).save(chat);
    }

    @Test
    @SneakyThrows
    void removeBotFromChannelTest() {
        /*Бот удален с канала*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("left");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).remove(chat.getId());
    }

    @Test
    @SneakyThrows
    void kickBotFromChannelTest() {
        /*Бот кикнут с канала*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("kicked");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).remove(chat.getId());
    }

    @Test
    void unknownStatusTest() {
        /*Прошла неизвестная команда*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("unknown");
        assertThrows(NoAvailableActionSendMessageException.class,
                () -> channelConnectHandler.processUpdate(update));
    }

    @Test
    void notBotIdTest() {
        /*id не бота*/
        botUser.setId(9L);
        assertThrows(NoAvailableActionSendMessageException.class,
                () -> channelConnectHandler.processUpdate(update));
    }

}