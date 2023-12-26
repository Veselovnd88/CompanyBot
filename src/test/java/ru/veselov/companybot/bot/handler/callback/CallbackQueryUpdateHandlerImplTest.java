package ru.veselov.companybot.bot.handler.callback;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.callback.impl.CallbackQueryUpdateHandlerImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedCallbackException;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CallbackQueryUpdateHandlerImplTest {

    @Mock
    CallbackQueryHandlerContext callbackQueryHandlerContext;

    @Mock
    UserDataCacheFacade userDataCache;

    @InjectMocks
    CallbackQueryUpdateHandlerImpl callbackQueryUpdateHandler;

    User user;

    Long userId;

    @BeforeEach
    void init() {
        user = TestUtils.getSimpleUser();
        userId = user.getId();
    }

    @Test
    void shouldChooseCorrectHandlerFromCallbackDataContext() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        String testCallbackDate = "boom";
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(testCallbackDate);
        Mockito.when(callbackQueryHandlerContext.getFromDataContext(testCallbackDate)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(botState));

        callbackQueryUpdateHandler.processUpdate(update);

        Mockito.verify(handlerMock).processUpdate(update);
    }

    @Test
    void shouldChooseCorrectHandlerFromBotStateContext() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("boom");
        Mockito.when(callbackQueryHandlerContext.getFromBotStateContext(botState)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(botState));

        callbackQueryUpdateHandler.processUpdate(update);

        Mockito.verify(handlerMock).processUpdate(update);
    }

    @Test
    void shouldThrowExceptionIfNotSuitableBotstateForHandler() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("boom");
        Mockito.when(callbackQueryHandlerContext.getFromBotStateContext(botState)).thenReturn(handlerMock);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(BotState.AWAIT_NAME));

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThatThrownBy(() -> callbackQueryUpdateHandler.processUpdate(update))
                        .isInstanceOf(UnexpectedCallbackException.class),
                () -> Mockito.verify(handlerMock, Mockito.never()).processUpdate(update)
        );
    }

    @Test
    void shouldThrowExceptionIfNoHandlerFoundForCallback() {
        BotState botState = BotState.AWAIT_CONTACT;
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("boom");
        Mockito.when(callbackQueryHandlerContext.getFromBotStateContext(botState)).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThatThrownBy(() -> callbackQueryUpdateHandler.processUpdate(update))
                        .isInstanceOf(UnexpectedCallbackException.class),
                () -> Mockito.verifyNoInteractions(handlerMock)
        );
    }

}
