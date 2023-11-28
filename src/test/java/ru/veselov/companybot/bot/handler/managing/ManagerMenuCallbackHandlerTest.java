package ru.veselov.companybot.bot.handler.managing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.cache.UserDataCache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class ManagerMenuCallbackHandlerTest {

    @MockBean
    CompanyBot bot;

    @Autowired
    UserDataCache userDataCache;
    @Autowired
    ManagerMenuCallbackHandler managerMenuCallbackHandler;

    Update update;
    CallbackQuery callbackQuery;
    User user;


    @BeforeEach
    void init(){
        update = spy(Update.class);
        callbackQuery = spy(CallbackQuery.class);
        user = spy(User.class);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
        user.setId(100L);
    }

    @Test
    void saveButtonTest(){
        callbackQuery.setData("saveManager");
        assertInstanceOf(SendMessage.class,managerMenuCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_MANAGER,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    void deleteButtonTest(){
        callbackQuery.setData("deleteManager");
        assertInstanceOf(SendMessage.class,managerMenuCallbackHandler.processUpdate(update));
        assertEquals(BotState.DELETE_MANAGER,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    void exitButtonTest(){
        callbackQuery.setData("exit");
        assertInstanceOf(SendMessage.class,managerMenuCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void showButtonTest(){
        callbackQuery.setData("show");
        assertInstanceOf(SendMessage.class,managerMenuCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

}