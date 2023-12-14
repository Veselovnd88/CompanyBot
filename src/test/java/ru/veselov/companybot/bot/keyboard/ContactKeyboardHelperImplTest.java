package ru.veselov.companybot.bot.keyboard;

import com.vdurmont.emoji.EmojiParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.util.MessageUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ContactKeyboardHelperImplTest {

    Update update;
    CallbackQuery callbackQuery;
    User user;
    Message message;
    Chat chat;

    ContactKeyboardHelperImpl contactKeyboardHelper;

    @BeforeEach
    void init() {
        Long userId = 100L;
        update = Mockito.spy(Update.class);
        callbackQuery = Mockito.spy(CallbackQuery.class);
        user = Mockito.spy(User.class);
        message = Mockito.spy(Message.class);
        chat = Mockito.spy(Chat.class);
        chat.setId(userId);
        callbackQuery.setMessage(message);
        message.setChat(chat);
        message.setMessageId(1000);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
        user.setId(userId);
        contactKeyboardHelper = new ContactKeyboardHelperImpl();
    }

    @ParameterizedTest
    @MethodSource("getRowsAndNamesWithMessages")
    void shouldCreateContactKeyboard(String callbackName, Integer buttonRow, String expectedMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = contactKeyboardHelper.getContactKeyboard();
        List<List<InlineKeyboardButton>> keyboard = inlineKeyboardMarkup.getKeyboard();
        String callbackData = keyboard.get(buttonRow).get(0).getCallbackData();
        String message = keyboard.get(buttonRow).get(0).getText();
        Assertions.assertThat(callbackData).isEqualTo(callbackName);
        Assertions.assertThat(message).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("getRowsAndNamesWhichCanMeMarked")
    void shouldMarkAndUnmarkField(String callbackName, Integer buttonRow) {
        EditMessageReplyMarkup editMessageReplyMarkup = contactKeyboardHelper
                .getEditMessageReplyForChosenCallbackButton(update, callbackName);
        //first time we check if field successfully marked
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getReplyMarkup().getKeyboard();
        String buttonText = keyboard.get(buttonRow).get(0).getText();
        Assertions.assertThat(buttonText).startsWith("<<").doesNotStartWith("<<<");
        for (int i = 0; i < 4; i++) {
            if (i != buttonRow) {
                Assertions.assertThat(keyboard.get(i).get(0).getText()).doesNotStartWith("<<");
            }
        }
        //We will call func for other fields and check if main field clear from bracers
        List<String> markableButtons = getMarkableButtons();
        for (String anotherButton : markableButtons) {
            if (!anotherButton.equals(callbackName)) {
                EditMessageReplyMarkup editMessageReplyMarkupSecondPress = contactKeyboardHelper
                        .getEditMessageReplyForChosenCallbackButton(update, anotherButton);
                List<List<InlineKeyboardButton>> keyboardAfterSecondPress = editMessageReplyMarkupSecondPress
                        .getReplyMarkup().getKeyboard();
                String buttonTextSecondPress = keyboardAfterSecondPress.get(buttonRow).get(0).getText();
                Assertions.assertThat(buttonTextSecondPress).doesNotStartWith("<<");
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getRowsAndNamesWhichCanMeMarked")
    void shouldMarkButtonAfterInputData(String callbackName, Integer buttonRow) {
        //create keyboard and mark button
        contactKeyboardHelper.getEditMessageReplyForChosenCallbackButton(update, callbackName);
        EditMessageReplyMarkup editMessageReplyAfterSendingContactData = contactKeyboardHelper
                .getEditMessageReplyAfterSendingContactData(user.getId(), callbackName);

        InlineKeyboardButton button = editMessageReplyAfterSendingContactData.getReplyMarkup()
                .getKeyboard().get(buttonRow).get(0);

        Assertions.assertThat(EmojiParser.parseToAliases(button.getText())).startsWith(":white_check_mark:")
                .doesNotStartWith(":white_check_mark:<<")
                .doesNotEndWith(">>");
    }

    @Test
    void shouldClearKeyboardCache() {
        Object keyboardMessageCache = ReflectionTestUtils.getField(contactKeyboardHelper, "keyboardMessageCache");
        Map<Long, EditMessageReplyMarkup> cacheMap = (Map<Long, EditMessageReplyMarkup>) keyboardMessageCache;

        Assertions.assertThat(cacheMap).isEmpty();
        contactKeyboardHelper
                .getEditMessageReplyForChosenCallbackButton(update, CallBackButtonUtils.PHONE);
        Assertions.assertThat(cacheMap).hasSize(1);
        contactKeyboardHelper.clear(user.getId());
        Assertions.assertThat(cacheMap).isEmpty();
    }

    private static Stream<Arguments> getRowsAndNamesWithMessages() {
        return Stream.of(
                Arguments.of(CallBackButtonUtils.NAME, 0, MessageUtils.INPUT_FIO),
                Arguments.of(CallBackButtonUtils.EMAIL, 1, MessageUtils.INPUT_EMAIL),
                Arguments.of(CallBackButtonUtils.PHONE, 2, MessageUtils.INPUT_PHONE),
                Arguments.of(CallBackButtonUtils.SHARED, 3, MessageUtils.ATTACH_CONTACT),
                Arguments.of(CallBackButtonUtils.SAVE, 4, MessageUtils.SAVE_AND_SEND)
        );
    }

    private static Stream<Arguments> getRowsAndNamesWhichCanMeMarked() {
        return Stream.of(
                Arguments.of(CallBackButtonUtils.NAME, 0),
                Arguments.of(CallBackButtonUtils.EMAIL, 1),
                Arguments.of(CallBackButtonUtils.PHONE, 2),
                Arguments.of(CallBackButtonUtils.SHARED, 3)
        );
    }

    private static List<String> getMarkableButtons() {
        return List.of(CallBackButtonUtils.NAME, CallBackButtonUtils.EMAIL, CallBackButtonUtils.SHARED,
                CallBackButtonUtils.PHONE);
    }
}
