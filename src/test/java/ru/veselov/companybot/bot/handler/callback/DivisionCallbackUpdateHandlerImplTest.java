package ru.veselov.companybot.bot.handler.callback;

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
import ru.veselov.companybot.bot.handler.callback.impl.DivisionCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.util.TestUtils;

import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class DivisionCallbackUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    DivisionKeyboardHelper divisionKeyboardHelper;

    @Mock
    BotStateHandlerContext context;

    @InjectMocks
    DivisionCallbackUpdateHandlerImpl divisionCallbackUpdateHandler;

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

    @Test
    void shouldRegisterInContext() {
        divisionCallbackUpdateHandler.registerInContext();

        Mockito.verify(context).add(BotState.AWAIT_DIVISION_FOR_INQUIRY, divisionCallbackUpdateHandler);
    }

    @Test
    void shouldProcessUpdateAndAddDivisionToInquiry() {
        DivisionModel division = TestUtils.getDivision();
        Mockito.when(divisionKeyboardHelper.getCachedDivisions())
                .thenReturn(Map.of(division.getDivisionId().toString(), division));
        callbackQuery.setData(division.getDivisionId().toString());

        divisionCallbackUpdateHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).createInquiry(user.getId(), division),
                () -> Mockito.verify(userDataCache).setUserBotState(user.getId(), BotState.AWAIT_MESSAGE)
        );
    }

    @Test
    void shouldThrowExceptionIfCallBackDivisionIdNotPresent() {
        DivisionModel division = TestUtils.getDivision();
        Mockito.when(divisionKeyboardHelper.getCachedDivisions())
                .thenReturn(Map.of(division.getDivisionId().toString(), division));
        callbackQuery.setData("not a division id");

        Assertions.assertThatThrownBy(() -> divisionCallbackUpdateHandler.processUpdate(update))
                .isInstanceOf(UnexpectedActionException.class);
    }

    @Test
    void getAvailableStates() {
        Set<BotState> availableStates = divisionCallbackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates).isEqualTo(Set.of(BotState.AWAIT_DIVISION_FOR_INQUIRY));
    }
}