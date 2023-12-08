package ru.veselov.companybot.bot.keyboard;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.keyboard.impl.DivisionKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DivisionKeyboardHelperTest {

    @Mock
    DivisionServiceImpl divisionService;

    @InjectMocks
    DivisionKeyboardHelperImpl divisionKeyboardHelper;

    Update update;
    CallbackQuery callbackQuery;
    User user;
    User userFrom;
    Message message;
    Chat chat;


    @BeforeEach
    void init() {
        Long userId = 100L;
        update = Mockito.spy(Update.class);
        callbackQuery = Mockito.spy(CallbackQuery.class);
        user = Mockito.spy(User.class);
        userFrom = Mockito.spy(User.class);
        userFrom.setId(101L);
        message = Mockito.spy(Message.class);
        chat = Mockito.spy(Chat.class);
        chat.setId(userId);
        callbackQuery.setMessage(message);
        message.setChat(chat);
        message.setMessageId(1000);
        message.setForwardFrom(userFrom);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
        update.setMessage(message);
        user.setId(userId);
    }

    @Test
    void shouldGetKeyboardWithDivisionsFromDB() {
        DivisionModel div1 = DivisionModel.builder()
                .divisionId(UUID.randomUUID()).name("Test").description("desc1").build();
        DivisionModel div2 = DivisionModel.builder()
                .divisionId(UUID.randomUUID()).name("Test2").description("desc2").build();
        Mockito.when(divisionService.findAll()).thenReturn(List.of(div1, div2));
        InlineKeyboardMarkup customerDivisionKeyboard = divisionKeyboardHelper.getCustomerDivisionKeyboard();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard()).hasSize(2),
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(0).get(0)
                        .getCallbackData()).isEqualTo(div1.getDivisionId().toString()),
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(0).get(0)
                        .getText()).isEqualTo(div1.getDescription()),

                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(1).get(0)
                        .getCallbackData()).isEqualTo(div2.getDivisionId().toString()),
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(1).get(0)
                        .getText()).isEqualTo(div2.getDescription())
        );
    }

    @Test
    void shouldGetKeyboardWithOnePreInstalledDivision() {
        InlineKeyboardMarkup customerDivisionKeyboard = divisionKeyboardHelper.getCustomerDivisionKeyboard();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard()).hasSize(1),
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(0).get(0)
                        .getCallbackData()).isNotBlank(),
                () -> Assertions.assertThat(customerDivisionKeyboard.getKeyboard().get(0).get(0)
                        .getText()).isEqualTo(MessageUtils.COMMON_DIV)
        );
    }

}
