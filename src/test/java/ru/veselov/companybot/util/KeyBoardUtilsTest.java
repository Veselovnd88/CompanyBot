package ru.veselov.companybot.util;

import com.vdurmont.emoji.EmojiParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.util.KeyBoardUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class KeyBoardUtilsTest {
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private KeyBoardUtils keyBoardUtils;



    Update update;
    CallbackQuery callbackQuery;
    User user;
    Message message;
    Chat chat;

    @BeforeEach
    void init(){
        Long userId = 100L;
        update=spy(Update.class);
        callbackQuery = spy(CallbackQuery.class);
        user=spy(User.class);
        message = spy(Message.class);
        chat = spy(Chat.class);
        chat.setId(userId);
        callbackQuery.setMessage(message);
        message.setChat(chat);
        message.setMessageId(1000);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
        user.setId(userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"name","email","phone","shared"})
    void testChoseRow(String field){
        /*Проверка на то что скобки на кнопках клавиатуры изменяются корректно, при перепрыгивании
        * не добавляются новые, убираются с предыдущей кнопки*/
        keyBoardUtils.editMessageChooseField(update,field);
        int rowIndex = keyBoardUtils.getRowIndexForTest(field);
        EditMessageReplyMarkup editMessageReplyMarkup = keyBoardUtils.getKeyboardMessageCache().get(user.getId());
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getReplyMarkup().getKeyboard();
        assertTrue(keyboard.get(rowIndex).get(0).getText().startsWith("<<"));
        assertFalse(keyboard.get(rowIndex).get(0).getText().startsWith("<<<"));
        for(int i=0; i<4;i++){
            if(i!=rowIndex){
                assertFalse(keyboard.get(i).get(0).getText().startsWith("<<"));}
        }
        keyBoardUtils.editMessageChooseField(update,field);
        assertFalse(keyboard.get(rowIndex).get(0).getText().startsWith("<<<"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name","email","phone","shared"})
    void saveRowMarkTest(String field){
        keyBoardUtils.editMessageChooseField(update,field);
        int rowIndex = keyBoardUtils.getRowIndexForTest(field);
        EditMessageReplyMarkup editMessageReplyMarkup = keyBoardUtils.getKeyboardMessageCache().get(user.getId());
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getReplyMarkup().getKeyboard();
        keyBoardUtils.editMessageSavedField(user.getId(), field);
        assertTrue(EmojiParser.parseToAliases(keyboard.get(rowIndex).get(0).getText()).startsWith(":white_check_mark:"));
        assertFalse(keyboard.get(rowIndex).get(0).getText().startsWith("<<"));
        keyBoardUtils.editMessageChooseField(update,field);
        assertTrue(keyboard.get(rowIndex).get(0).getText().startsWith("<<"));
        keyBoardUtils.editMessageSavedField(user.getId(), field);
        assertFalse(EmojiParser.parseToAliases(keyboard.get(rowIndex).get(0).getText()).startsWith(":white_check_mark::white_check_mark:"));
    }


}