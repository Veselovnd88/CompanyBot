package ru.veselov.companybot.bot.handler.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class ContactCallbackUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    KeyBoardUtils keyBoardUtils;

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
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @ParameterizedTest
    @ValueSource(strings = {CallBackButtonUtils.CONTACT, CallBackButtonUtils.REPEAT})
    void shouldHandleCallBackDataAndInviteToInputContactData(String data) {
        callbackQuery.setData(data);

        SendMessage sendMessage = (SendMessage) contactCallbackUpdateHandler.processUpdate(update);

        Long userId = user.getId();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.INPUT_CONTACT),
                () -> Mockito.verify(userDataCache).setUserBotState(userId, BotState.AWAIT_CONTACT),
                () -> Mockito.verify(userDataCache).createContact(userId),
                () -> Mockito.verify(keyBoardUtils).contactKeyBoard()
        );
    }

}
