package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionKeyboardUtilsTest {
    @Autowired
    private DivisionKeyboardUtils divisionKeyboardUtils;
    @MockBean
    private DivisionService divisionService;
    @MockBean
    CompanyBot companyBot;

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

        when(divisionService.findAll()).thenReturn(List.of(
                Division.builder().divisionId("T").name("Test").build(),
                Division.builder().divisionId("T2").name("Test2").build()
        ));
    }


    @Test
    void createKeyboardTest(){
        InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.departmentKeyboard();
        assertInstanceOf(InlineKeyboardMarkup.class, inlineKeyboardMarkup);
        assertEquals(4,inlineKeyboardMarkup.getKeyboard().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test","Test2"})
    void pressButtonExceptNoneTest(String field){
        EditMessageReplyMarkup editMessageReplyMarkup = divisionKeyboardUtils.divisionChooseField(update, field);
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getReplyMarkup().getKeyboard();
        for(var row : keyboard){
            if(row.get(0).getText().equalsIgnoreCase(field)){
                assertTrue(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
            }
        }
    }

    //TODO продолжить тестирование клавиатуры

}