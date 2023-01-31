package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WorkFlowTest {

    @MockBean
    CompanyBot bot;
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @BeforeEach
    void init(){

    }
    @Test
    void workFlowTest(){
        UserActions userActions = new UserActions();
        User user  = new User();
        user.setUserName("Vasya");
        user.setLastName("Petya");
        user.setId(100L);
        assertInstanceOf(SendMessage.class,
                telegramFacadeUpdateHandler.processUpdate(userActions.userPressStart(user)));
        assertEquals(BotState.READY, userDataCache.getUserBotState(user.getId()));
        assertInstanceOf(SendMessage.class,
                telegramFacadeUpdateHandler.processUpdate(userActions.userPressInquiry(user)));
        assertEquals(BotState.AWAIT_DIVISION_FOR_INQUIRY, userDataCache.getUserBotState(user.getId()));
        assertInstanceOf(SendMessage.class,
                telegramFacadeUpdateHandler.processUpdate(userActions.userPressInquiryButton(user)));
        assertEquals(BotState.AWAIT_MESSAGE, userDataCache.getUserBotState(user.getId()));
        telegramFacadeUpdateHandler.processUpdate(userActions.userPressInquiryButton2(user));
        for(int i=0; i<5; i++){
            telegramFacadeUpdateHandler.processUpdate(userActions.userSendMessage(user));
        }
        assertEquals(BotState.AWAIT_MESSAGE,userDataCache.getUserBotState(user.getId()));
        telegramFacadeUpdateHandler.processUpdate(userActions.userPressContactButton(user));
        assertEquals(BotState.AWAIT_CONTACT, userDataCache.getUserBotState(user.getId()));
    }


}
