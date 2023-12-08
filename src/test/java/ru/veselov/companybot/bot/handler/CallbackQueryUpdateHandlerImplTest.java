package ru.veselov.companybot.bot.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.impl.CallbackQueryUpdateHandlerImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CallbackQueryUpdateHandlerImplTest {

    @Mock
    BotStateHandlerContext botStateHandlerContext;

    @Mock
    CallbackQueryDataHandlerContext callbackQueryDataHandlerContext;

    @Mock
    UserDataCacheFacade userDataCache;

    @InjectMocks
    CallbackQueryUpdateHandlerImpl callbackQueryUpdateHandler;


    Update update;
    CallbackQuery callbackQuery;
    User user;

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        callbackQuery = Mockito.spy(CallbackQuery.class);
        user = Mockito.spy(User.class);
        user.setId(TestUtils.USER_ID);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
    }

    @Test
    void shouldChooseCorrectHandlerFromCallbackDataContext() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        String testCallbackDate = "hi";
        callbackQuery.setData(testCallbackDate);
        Mockito.when(callbackQueryDataHandlerContext.getHandler(testCallbackDate)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(botState));

        callbackQueryUpdateHandler.processUpdate(update);

        Mockito.verify(handlerMock).processUpdate(update);
    }

    @Test
    void shouldChooseCorrectHandlerFromBotStateContext() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        callbackQuery.setData(null);
        Mockito.when(botStateHandlerContext.getHandler(botState)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(botState));

        callbackQueryUpdateHandler.processUpdate(update);

        Mockito.verify(handlerMock).processUpdate(update);
    }

    @Test
    void shouldThrowExceptionIfNotSuitableBotstateForHandler() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        callbackQuery.setData(null);
        Mockito.when(botStateHandlerContext.getHandler(botState)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(BotState.AWAIT_NAME));

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThatThrownBy(() -> callbackQueryUpdateHandler.processUpdate(update))
                        .isInstanceOf(UnexpectedActionException.class),
                () -> Mockito.verify(handlerMock, Mockito.never()).processUpdate(update)
        );
    }

    @Test
    void shouldThrowExceptionIfNoHandlerFoundForCallback() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        callbackQuery.setData(null);
        Mockito.when(botStateHandlerContext.getHandler(botState)).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThatThrownBy(() -> callbackQueryUpdateHandler.processUpdate(update))
                        .isInstanceOf(UnexpectedActionException.class),
                () -> Mockito.verifyNoInteractions(handlerMock)
        );
    }


}