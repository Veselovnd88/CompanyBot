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
import ru.veselov.companybot.bot.handler.callback.impl.ContactCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ContactCallbackUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    ContactKeyboardHelperImpl contactKeyboardHelper;

    @Mock
    CallbackQueryHandlerContext context;

    @InjectMocks
    ContactCallbackUpdateHandlerImpl contactCallbackUpdateHandler;

    Long userId;

    @BeforeEach
    void init() {
        userId = TestUtils.getSimpleUser().getId();
    }

    @Test
    void shouldCallbackDataSetUpStateCreateContactAndReturnKeyboardForInputData() {
        Update update = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser("smth");
        contactCallbackUpdateHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).setUserBotState(userId, BotState.AWAIT_CONTACT),
                () -> Mockito.verify(userDataCache).createContact(userId),
                () -> Mockito.verify(contactKeyboardHelper).getContactKeyboard()
        );
    }

    @Test
    void shouldRegisterInContext() {
        contactCallbackUpdateHandler.registerInContext();

        Mockito.verify(context).addToDataContext(CallBackButtonUtils.CONTACT, contactCallbackUpdateHandler);
        Mockito.verify(context).addToDataContext(CallBackButtonUtils.REPEAT, contactCallbackUpdateHandler);
    }

    @Test
    void shouldReturnAvailableStates() {
        Set<BotState> availableStates = contactCallbackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates)
                .isEqualTo(Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                        BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE));
    }

}
