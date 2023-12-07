package ru.veselov.companybot.bot.handler.impl;

import org.assertj.core.api.Assertions;
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
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.util.TestUtils;

import java.util.stream.Stream;

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
    @MethodSource("getFieldNameAndStatus")
    void shouldHandleCallbackDataToMarkAndUnmarkField(String field, BotState botState) {
        callbackQuery.setData(field);

        inputContactCallBackUpdateHandler.processUpdate(update);

        Mockito.verify(keyBoardUtils).editMessageChooseField(update, field);
        Mockito.verify(userDataCache).setUserBotState(user.getId(), botState);
    }

    @Test
    void shouldThrowExceptionIfUnexpectedCallBackDataWasPassed() {
        callbackQuery.setData("string");

        Assertions.assertThatThrownBy(() -> inputContactCallBackUpdateHandler.processUpdate(update))
                .isInstanceOf(UnexpectedActionException.class);
    }

    private static Stream<Arguments> getFieldNameAndStatus() {
        return Stream.of(
                Arguments.of(CallBackButtonUtils.EMAIL, BotState.AWAIT_EMAIL),
                Arguments.of(CallBackButtonUtils.PHONE, BotState.AWAIT_PHONE),
                Arguments.of(CallBackButtonUtils.NAME, BotState.AWAIT_NAME),
                Arguments.of(CallBackButtonUtils.SHARED, BotState.AWAIT_SHARED));
    }


}