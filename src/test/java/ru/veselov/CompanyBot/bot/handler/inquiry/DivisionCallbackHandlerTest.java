package ru.veselov.CompanyBot.bot.handler.inquiry;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
import ru.veselov.CompanyBot.exception.NoAvailableActionCallbackException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionCallbackHandlerTest {
    @MockBean
    CompanyBot bot;
    @MockBean
    CommandLineRunner commandLineRunner;
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    DivisionCallbackHandler divisionCallbackHandler;
    @MockBean
    DivisionKeyboardUtils divisionKeyboardUtils;
    Update update;
    CallbackQuery callbackQuery;
    User user;
    HashMap<String,DivisionModel> divs=new HashMap<>();


    @BeforeEach
    void init() throws NoDivisionsException {
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(100L);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
        divs.put("L", DivisionModel.builder().divisionId("L").build());
        when(divisionKeyboardUtils.getCachedDivisions()).thenReturn(divs);
    }

    @Test
    @SneakyThrows
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
        assertThrows(NoAvailableActionCallbackException.class,
                ()->divisionCallbackHandler.processUpdate(update));
        assertNull(userDataCache.getInquiry(user.getId()));
    }

}