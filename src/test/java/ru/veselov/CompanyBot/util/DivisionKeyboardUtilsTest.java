package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import javax.annotation.PostConstruct;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionKeyboardUtilsTest {

    @Autowired
    DivisionKeyboardUtils divisionKeyboardUtils;
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
        InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.divisionKeyboard();
        assertInstanceOf(InlineKeyboardMarkup.class, inlineKeyboardMarkup);
        assertEquals(3,inlineKeyboardMarkup.getKeyboard().size());
    }

    @Test
    void pressNoneButtonWithMarksTest(){
        //Нажатие кнопки "none" должно убирать пометки со всех кнопок
        divisionKeyboardUtils.divisionChooseField(update,"T");
        divisionKeyboardUtils.divisionChooseField(update,"T2");
        EditMessageReplyMarkup secondPress = divisionKeyboardUtils.divisionChooseField(update, "none");
        List<List<InlineKeyboardButton>> secondPressKeyboard = secondPress.getReplyMarkup().getKeyboard();
        for(var row : secondPressKeyboard){
            assertFalse(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
        }
    }

    @Test
    void getMarkedButtonsTest(){
        //Проверка выдачи кнопок с отметками
        divisionKeyboardUtils.divisionChooseField(update,"T");
        divisionKeyboardUtils.divisionChooseField(update,"T2");
        assertEquals(2,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
        divisionKeyboardUtils.divisionChooseField(update,"T+marked");
        assertEquals(1,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
        divisionKeyboardUtils.divisionChooseField(update,"T2+marked");
        assertEquals(0,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
        divisionKeyboardUtils.divisionChooseField(update,"T");
        divisionKeyboardUtils.divisionChooseField(update,"none");
        assertEquals(0,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
    }


}