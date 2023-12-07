package ru.veselov.companybot.bot.handler.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.service.CustomerDataHandler;
import ru.veselov.companybot.util.TestUtils;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ContactCallbackUpdateHandlerImplTest {

    @Mock
    CustomerDataHandler customerDataHandler;

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
    @MethodSource("getFieldStrings")
    void shouldHandleCallbackDataToMarkAndUnmarkChosenField(String field) {
        callbackQuery.setData(field);

        contactCallbackUpdateHandler.processUpdate(update);

        Mockito.verify(keyBoardUtils).editMessageChooseField(update, field);
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
                () -> Mockito.verify(userDataCache).createContact(userId)
        );
    }

    @Test
    void shouldHandleSaveButtonEvent() {
        callbackQuery.setData(CallBackButtonUtils.SAVE);

        AnswerCallbackQuery answerCallBack = (AnswerCallbackQuery) contactCallbackUpdateHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(answerCallBack.getText()).isEqualTo(MessageUtils.SAVED),
                () -> Mockito.verify(customerDataHandler).handle(user.getId())
        );
    }

    @Test
    void shouldThrowExceptionIfUnexpectedCallBackDataWasPassed() {
        callbackQuery.setData("string");

        Assertions.assertThatThrownBy(() -> contactCallbackUpdateHandler.processUpdate(update))
                .isInstanceOf(UnexpectedActionException.class);
    }

    private static Stream<String> getFieldStrings() {
        return Stream.of(
                CallBackButtonUtils.NAME,
                CallBackButtonUtils.EMAIL,
                CallBackButtonUtils.PHONE,
                CallBackButtonUtils.SHARED
        );
    }


}