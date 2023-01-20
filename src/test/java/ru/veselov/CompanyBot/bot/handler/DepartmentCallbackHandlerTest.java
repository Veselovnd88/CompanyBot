package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DepartmentCallbackHandlerTest {
    @MockBean
    CompanyBot bot;
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    DivisionCallbackHandler divisionCallbackHandler;
    @MockBean
    DivisionKeyboardUtils divisionKeyboardUtils;
    Update update;
    CallbackQuery callbackQuery;
    User user;
    HashMap<String,Division> divs=new HashMap<>();


    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(100L);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
        divs.put("L", Division.builder().divisionId("L").build());
        when(divisionKeyboardUtils.getCachedDivisions()).thenReturn(divs);
    }

    @Test
    void departmentChosenTest(){
        callbackQuery.setData("L");
        BotApiMethod<?> botApiMethod = divisionCallbackHandler.processUpdate(update);
        assertInstanceOf(SendMessage.class,botApiMethod);
        assertNotNull(userDataCache.getInquiry(user.getId()));
        assertEquals(BotState.AWAIT_MESSAGE,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void departmentWrongTest(){
        userDataCache.clear(user.getId());
        callbackQuery.setData("wrong");
        BotApiMethod<?> botApiMethod = divisionCallbackHandler.processUpdate(update);
        assertInstanceOf(AnswerCallbackQuery.class,botApiMethod);
        assertNull(userDataCache.getInquiry(user.getId()));
    }


}