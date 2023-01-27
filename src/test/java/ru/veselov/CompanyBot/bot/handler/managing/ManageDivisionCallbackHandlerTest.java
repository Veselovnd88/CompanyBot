package ru.veselov.CompanyBot.bot.handler.managing;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ManageDivisionCallbackHandlerTest {
    @MockBean
    CompanyBot bot;
    @Autowired
    private UserDataCache userDataCache;
    @MockBean
    DivisionService divisionService;

    @Autowired
    DivisionKeyboardUtils divisionKeyboardUtils;
    @Autowired
    ManageDivisionCallbackHandler manageDivisionCallbackHandler;

    Update update;
    CallbackQuery callbackQuery;
    User user;


    @BeforeEach
    void init(){
        user = spy(User.class);
        update = spy(Update.class);
        callbackQuery = spy(CallbackQuery.class);
        callbackQuery.setFrom(user);
        callbackQuery.setId("123123123");
        user.setId(100L);
        update.setCallbackQuery(callbackQuery);
        when(divisionService.findAll()).thenReturn(List.of(
                DivisionModel.builder().divisionId("L").name("LOLO").build(),
                DivisionModel.builder().divisionId("T").name("TOTO").build()
        ));

    }

    @ParameterizedTest
    @ValueSource(strings = {"L","T"})
    void removeDivisionTest(String data){
        userDataCache.setUserBotState(user.getId(), BotState.DELETE_DIV);
        callbackQuery.setData(data);
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        verify(divisionService).remove(any(DivisionModel.class));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void noDivisionInDBTest(){
        userDataCache.setUserBotState(user.getId(), BotState.DELETE_DIV);
        callbackQuery.setData("L");
        when(divisionService.findAll()).thenReturn(Collections.EMPTY_LIST);
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        verify(divisionService,never()).remove(any(DivisionModel.class));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void addButtonTest(){
        userDataCache.setUserBotState(user.getId(), BotState.MANAGE);
        callbackQuery.setData("addDivision");
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_DIVISION,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void deleteButtonTest(){
        userDataCache.setUserBotState(user.getId(), BotState.MANAGE);
        callbackQuery.setData("deleteDivision");
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        assertEquals(BotState.DELETE_DIV,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void deleteButtonNoDivisionsInDBTest(){
        userDataCache.setUserBotState(user.getId(), BotState.MANAGE);
        callbackQuery.setData("deleteDivision");
        when(divisionService.findAll()).thenReturn(Collections.EMPTY_LIST);
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void exitButtonTest(){
        userDataCache.setUserBotState(user.getId(), BotState.MANAGE);
        callbackQuery.setData("exit");
        assertInstanceOf(SendMessage.class,manageDivisionCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void wrongDataFromCallback(){
        userDataCache.setUserBotState(user.getId(), BotState.MANAGE);
        callbackQuery.setData("Something Wrong");
        assertInstanceOf(AnswerCallbackQuery.class,manageDivisionCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }


}