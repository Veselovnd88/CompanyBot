package ru.veselov.companybot.bot.handler.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateMessageHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.message.impl.MessageUpdateHandlerImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedCallbackException;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.Collections;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class MessageUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    CommandUpdateHandler commandUpdateHandler;

    @Mock
    BotStateMessageHandlerContext botStateMessageHandlerContext;

    @InjectMocks
    MessageUpdateHandlerImpl messageUpdateHandler;

    Long userId;

    @BeforeEach
    void init() {
        userId = TestUtils.getSimpleUser().getId();
    }

    @Test
    void shouldChooseMessageHandlerFromContext() {
        BotState botState = BotState.AWAIT_MESSAGE;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Set.of(botState));
        Mockito.when(botStateMessageHandlerContext.getFromBotStateContext(Mockito.any())).thenReturn(handlerMock);
        Update update = TestUpdates.getUpdateWithMessageWithTextContentByUser();

        messageUpdateHandler.processUpdate(update);

        Mockito.verify(handlerMock).processUpdate(update);
        Mockito.verifyNoInteractions(commandUpdateHandler);
    }

    @Test
    void shouldThrowExceptionIfStateNotSupportedByHandler() {
        BotState botState = BotState.AWAIT_MESSAGE;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        UpdateHandlerFromContext handlerMock = Mockito.mock(UpdateHandlerFromContext.class);
        Mockito.when(handlerMock.getAvailableStates()).thenReturn(Collections.emptySet());
        Mockito.when(botStateMessageHandlerContext.getFromBotStateContext(Mockito.any())).thenReturn(handlerMock);
        Update update = TestUpdates.getUpdateWithMessageWithTextContentByUser();

        Assertions.assertThatThrownBy(() -> messageUpdateHandler.processUpdate(update))
                .isInstanceOf(UnexpectedCallbackException.class);
        Mockito.verifyNoInteractions(commandUpdateHandler, commandUpdateHandler);
        Mockito.verify(handlerMock, Mockito.never()).processUpdate(update);
    }

    @Test
    void shouldThrowExceptionIfNoAvailableHandlerInContext() {
        BotState botState = BotState.AWAIT_MESSAGE;
        Mockito.when(userDataCache.getUserBotState(userId)).thenReturn(botState);
        Mockito.when(botStateMessageHandlerContext.getFromBotStateContext(Mockito.any())).thenReturn(null);
        Update update = TestUpdates.getUpdateWithMessageWithTextContentByUser();

        Assertions.assertThatThrownBy(() -> messageUpdateHandler.processUpdate(update))
                .isInstanceOf(UnexpectedCallbackException.class);
        Mockito.verifyNoInteractions(commandUpdateHandler, commandUpdateHandler);
    }

    @Test
    void shouldCallCommandUpdateHandlerIfMessageHasCommandEntity() {
        Update updateWithMessageWithCommandByUser = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);

        messageUpdateHandler.processUpdate(updateWithMessageWithCommandByUser);

        Mockito.verify(commandUpdateHandler).processUpdate(updateWithMessageWithCommandByUser);
    }

}
