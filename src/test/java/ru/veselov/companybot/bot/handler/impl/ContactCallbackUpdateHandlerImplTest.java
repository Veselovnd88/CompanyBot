package ru.veselov.companybot.bot.handler.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
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
    void shouldHandleCallbackDataToMarkChosenField(String field) {
        callbackQuery.setData(field);
        contactCallbackUpdateHandler.processUpdate(update);

        Mockito.verify(keyBoardUtils).editMessageChooseField(update, field);
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