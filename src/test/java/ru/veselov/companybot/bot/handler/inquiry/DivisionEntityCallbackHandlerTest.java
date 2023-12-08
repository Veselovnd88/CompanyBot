package ru.veselov.companybot.bot.handler.inquiry;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.callback.impl.DivisionCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.exception.NoDivisionsException;
import ru.veselov.companybot.model.DivisionModel;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionEntityCallbackHandlerTest {
    @MockBean
    CompanyBot bot;
    @MockBean
    CommandLineRunner commandLineRunner;
    @Autowired
    UserDataCacheFacade userDataCacheFacade;
    @Autowired
    DivisionCallbackUpdateHandlerImpl divisionCallbackHandler;
    @MockBean
    DivisionKeyboardHelper divisionKeyboardHelper;
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
        divs.put(UUID.randomUUID().toString(), DivisionModel.builder().divisionId(UUID.randomUUID()).build());
        when(divisionKeyboardHelper.getCachedDivisions()).thenReturn(divs);
    }

    @Test
    @SneakyThrows
    void departmentChosenTest(){
        callbackQuery.setData("L");
        BotApiMethod<?> botApiMethod = divisionCallbackHandler.processUpdate(update);
        assertInstanceOf(SendMessage.class,botApiMethod);
        assertNotNull(userDataCacheFacade.getInquiry(user.getId()));
        assertEquals(BotState.AWAIT_MESSAGE, userDataCacheFacade.getUserBotState(user.getId()));
    }

    @Test
    void departmentWrongTest(){
        userDataCacheFacade.clear(user.getId());
        callbackQuery.setData("wrong");
        assertThrows(NoAvailableActionCallbackException.class,
                ()->divisionCallbackHandler.processUpdate(update));
        assertNull(userDataCacheFacade.getInquiry(user.getId()));
    }

}