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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.handler.callback.impl.ContactCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ContactCallbackUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    KeyBoardUtils keyBoardUtils;

    @Mock
    CallbackQueryDataHandlerContext context;

    @InjectMocks
    ContactCallbackUpdateHandlerImpl contactCallbackUpdateHandler;

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
        Message message = new Message();
        message.setMessageId(100);
        message.setFrom(user);
        Chat chat = new Chat();
        chat.setId(100L);
        message.setChat(chat);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId("100");
    }

    @Test
    void shouldCallbackDataSetUpStateCreateContactAndReturnKeyboardForInputData() {
        contactCallbackUpdateHandler.processUpdate(update);

        Long userId = user.getId();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).setUserBotState(userId, BotState.AWAIT_CONTACT),
                () -> Mockito.verify(userDataCache).createContact(userId),
                () -> Mockito.verify(keyBoardUtils).contactKeyBoard()
        );
    }

    @Test
    void shouldRegisterInContext() {
        contactCallbackUpdateHandler.registerInContext();

        Mockito.verify(context).add(CallBackButtonUtils.CONTACT, contactCallbackUpdateHandler);
        Mockito.verify(context).add(CallBackButtonUtils.REPEAT, contactCallbackUpdateHandler);
    }

    @Test
    void shouldReturnAvailableStates() {
        Set<BotState> availableStates = contactCallbackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates)
                .isEqualTo(Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                        BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE));
    }

}
