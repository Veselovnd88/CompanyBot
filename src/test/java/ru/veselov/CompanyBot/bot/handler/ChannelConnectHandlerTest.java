package ru.veselov.CompanyBot.bot.handler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.service.ChatService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ChannelConnectHandlerTest {
    @MockBean
    private CompanyBot companyBot;

    @Autowired
    ChannelConnectHandler channelConnectHandler;

    @MockBean
    ChatService chatService;

    Update update;
    User user;
    User botUser;
    Message message;
    ChatMemberUpdated chatMemberUpdated;
    ChatMember chatMember;
    Chat chat;

    @BeforeEach
    void init(){
        update= spy(Update.class);
        user= spy(User.class);
        user.setId(100L);
        botUser=spy(User.class);
        message=spy(Message.class);
        chatMemberUpdated = spy(ChatMemberUpdated.class);
        chatMember= spy(ChatMember.class);
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
    void connectBotToChannelTest(){
        /*Бот присоединен к каналу админом*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("administrator");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).save(chat);
    }
    @Test
    void removeBotFromChannelTest(){
        /*Бот удален с канала*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("left");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).remove(chat.getId());
    }

    @Test
    void kickBotFromChannelTest(){
        /*Бот кикнут с канала*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("kicked");
        assertNotNull(channelConnectHandler.processUpdate(update));
        verify(chatService).remove(chat.getId());
    }
    @Test
    void unknownStatusTest(){
        /*Прошла неизвестная команда*/
        botUser.setId(companyBot.getBotId());
        when(chatMember.getStatus()).thenReturn("unknown");
        assertNull(channelConnectHandler.processUpdate(update));
    }

    @Test
    void notBotIdTest(){
        /*ID не бота*/
        botUser.setId(9L);
        assertNull(channelConnectHandler.processUpdate(update));
    }

}