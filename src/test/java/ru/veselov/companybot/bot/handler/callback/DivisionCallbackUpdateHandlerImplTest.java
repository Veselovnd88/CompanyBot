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
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.handler.callback.impl.DivisionCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.util.TestUpdates;
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
    CallbackQueryHandlerContext context;

    @InjectMocks
    DivisionCallbackUpdateHandlerImpl divisionCallbackUpdateHandler;

    Long userId;

    @BeforeEach
    void init() {
        userId = TestUtils.getSimpleUser().getId();
    }

    @Test
    void shouldRegisterInContext() {
        divisionCallbackUpdateHandler.registerInContext();

        Mockito.verify(context).addToBotStateContext(BotState.AWAIT_DIVISION_FOR_INQUIRY, divisionCallbackUpdateHandler);
    }

    @Test
    void shouldProcessUpdateAndAddDivisionToInquiry() {
        DivisionModel division = TestUtils.getDivision();
        Mockito.when(divisionKeyboardHelper.getCachedDivisions())
                .thenReturn(Map.of(division.getDivisionId().toString(), division));
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(division.getDivisionId().toString());

        divisionCallbackUpdateHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).createInquiry(userId, division),
                () -> Mockito.verify(userDataCache).setUserBotState(userId, BotState.AWAIT_MESSAGE)
        );
    }

    @Test
    void getAvailableStates() {
        Set<BotState> availableStates = divisionCallbackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates).isEqualTo(Set.of(BotState.AWAIT_DIVISION_FOR_INQUIRY));
    }
}