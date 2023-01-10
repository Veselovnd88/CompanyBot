package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.cache.UserDataCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class DepartmentCallbackHandlerTest {

    @Autowired
    UserDataCache userDataCache;
    @Autowired
    DepartmentCallbackHandler departmentCallbackHandler;
    Update update;
    CallbackQuery callbackQuery;
    User user;


    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(100L);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @Test
    void departmentChosenTest(){
        callbackQuery.setData("leuze");
        BotApiMethod<?> botApiMethod = departmentCallbackHandler.processUpdate(update);
        assertInstanceOf(SendMessage.class,botApiMethod);
        assertNotNull(userDataCache.getInquiry(user.getId()));
        assertEquals(BotState.AWAIT_MESSAGE,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void departmentWrongTest(){
        callbackQuery.setData("wrong");
        BotApiMethod<?> botApiMethod = departmentCallbackHandler.processUpdate(update);
        assertInstanceOf(AnswerCallbackQuery.class,botApiMethod);
        assertNull(userDataCache.getInquiry(user.getId()));
    }


}