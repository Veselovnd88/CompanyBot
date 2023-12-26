package ru.veselov.companybot.bot.handler.callback;

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
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.handler.callback.impl.InputContactCallBackUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class InputContactCallBackUpdateHandlerImplTest {

    @Mock
    ContactKeyboardHelperImpl contactKeyboardHelper;

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    CallbackQueryHandlerContext context;

    @InjectMocks
    InputContactCallBackUpdateHandlerImpl inputContactCallBackUpdateHandler;

    Long userId;

    @BeforeEach
    void init() {
        userId = TestUtils.getSimpleUser().getId();
    }

    @ParameterizedTest
    @MethodSource("getFieldNameAndStatus")
    void shouldHandleCallbackDataToMarkAndUnmarkField(String field, BotState botState) {
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(field);

        inputContactCallBackUpdateHandler.processUpdate(update);

        Mockito.verify(contactKeyboardHelper).getEditMessageReplyForChosenCallbackButton(update, field);
        Mockito.verify(userDataCache).setUserBotState(userId, botState);
    }

    @Test
    void shouldThrowExceptionIfUnexpectedCallBackDataWasPassed() {
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("string");

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

    @Test
    void shouldRegisterInContext() {
        inputContactCallBackUpdateHandler.registerInContext();

        Mockito.verify(context).addToDataContext(CallBackButtonUtils.EMAIL, inputContactCallBackUpdateHandler);
        Mockito.verify(context).addToDataContext(CallBackButtonUtils.PHONE, inputContactCallBackUpdateHandler);
        Mockito.verify(context).addToDataContext(CallBackButtonUtils.NAME, inputContactCallBackUpdateHandler);
        Mockito.verify(context).addToDataContext(CallBackButtonUtils.SHARED, inputContactCallBackUpdateHandler);
    }

    @Test
    void shouldReturnAvailableStates() {
        Set<BotState> availableStates = inputContactCallBackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates)
                .isEqualTo(Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                        BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE));
    }

}
