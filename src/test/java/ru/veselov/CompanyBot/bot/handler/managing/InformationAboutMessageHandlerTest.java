package ru.veselov.CompanyBot.bot.handler.managing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.CompanyInfoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class InformationAboutMessageHandlerTest {

    @MockBean
    CompanyBot companyBot;
    @MockBean
    CompanyInfoService companyInfoService;

    @Autowired
    InformationAboutMessageHandler informationAboutMessageHandler;
    @Autowired
    UserDataCache userDataCache;
    Update update;
    User user;
    Message message;

    @BeforeEach
    void init(){
        update=spy(Update.class);
        user = spy(User.class);
        message=spy(Message.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
        userDataCache.setUserBotState(user.getId(),BotState.MANAGE_ABOUT);
    }

    @Test
    void noTextTest(){
       assertInstanceOf(SendMessage.class, informationAboutMessageHandler.processUpdate(update));
       verify(companyInfoService,never()).save(any(Message.class));
       verify(message, never()).getText();
        assertEquals(BotState.MANAGE_ABOUT,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void tooLongTextTest(){
        String text = new String("1").repeat(905);
        message.setText(text);
        assertInstanceOf(SendMessage.class, informationAboutMessageHandler.processUpdate(update));
        verify(companyInfoService,never()).save(any(Message.class));
        assertEquals(BotState.MANAGE_ABOUT,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void correctTextTest(){
        String text = new String("1").repeat(800);
        message.setText(text);
        assertInstanceOf(SendMessage.class, informationAboutMessageHandler.processUpdate(update));
        verify(companyInfoService).save(any(Message.class));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
    }
}