package ru.veselov.companybot.bot.handler.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class InputContactCallBackUpdateHandlerImplTest {

    @Mock
    KeyBoardUtils keyBoardUtils;

    @Mock
    UserDataCacheFacade userDataCache;

    @InjectMocks
    InputContactCallBackUpdateHandlerImpl inputContactCallBackUpdateHandler;


    Update update;
    CallbackQuery callbackQuery;
    User user;

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        callbackQuery = Mockito.spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user = Mockito.spy(User.class);
        user.setId(TestUtils.USER_ID);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @ParameterizedTest
    @MethodSource
    void shouldHandleCallbackDataToMarkAndUnmarkField(Arguments arguments) {
        callbackQuery.setData(CallBackButtonUtils.EMAIL);

        inputContactCallBackUpdateHandler.processUpdate(update);

        Mockito.verify(keyBoardUtils).editMessageChooseField(update, CallBackButtonUtils.EMAIL);
        Mockito.verify(userDataCache).setUserBotState(user.getId(), BotState.AWAIT_EMAIL);
    }


}