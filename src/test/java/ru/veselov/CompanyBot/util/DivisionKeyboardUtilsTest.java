package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
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
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;
import ru.veselov.CompanyBot.exception.NoDivisionKeyboardException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionKeyboardUtilsTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    DivisionKeyboardUtils divisionKeyboardUtils;
    @MockBean
    private DivisionService divisionService;

    @MockBean
    private ManagerService managerService;

    Update update;
    CallbackQuery callbackQuery;
    User user;
    User userFrom;
    Message message;
    Chat chat;


    @BeforeEach
    void init(){
        Long userId = 100L;
        update=spy(Update.class);
        callbackQuery = spy(CallbackQuery.class);
        user=spy(User.class);
        userFrom=spy(User.class);
        userFrom.setId(101L);
        message = spy(Message.class);
        chat = spy(Chat.class);
        chat.setId(userId);
        callbackQuery.setMessage(message);
        message.setChat(chat);
        message.setMessageId(1000);
        message.setForwardFrom(userFrom);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
        update.setMessage(message);
        user.setId(userId);
        when(divisionService.findAll()).thenReturn(List.of(
                DivisionModel.builder().divisionId("T").name("Test").build(),
                DivisionModel.builder().divisionId("T2").name("Test2").build()
        ));
    }


    @Test
    void createKeyboardTestNoDivisions() throws NoDivisionsException {
        //Creating keyboard with no marked items
        InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),userFrom.getId());
        assertInstanceOf(InlineKeyboardMarkup.class, inlineKeyboardMarkup);
        assertEquals(3,inlineKeyboardMarkup.getKeyboard().size());
        for(var row : inlineKeyboardMarkup.getKeyboard()) {
            assertFalse(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
            assertFalse(row.get(0).getCallbackData().endsWith("marked"));}
    }
    @Test
    void createKeyboardWithNoDivisionsInDB() {
        when(divisionService.findAll()).thenReturn(new ArrayList<>());
        assertThrows(NoDivisionsException.class,
                ()-> divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),userFrom.getId()));
    }



    @Test
    @SneakyThrows
    void createKeyboardTestWithDivisions() {
        //Creating keyboard with marked items
        ManagerEntity managerEntity = new ManagerEntity();
        managerEntity.setManagerId(1L);
        managerEntity.setDivisions(Set.of(Division.builder().divisionId("T").name("Test").build()));
        when(managerService.findOneWithDivisions(userFrom.getId())).thenReturn(new ManagerModel());
        InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),userFrom.getId());
        assertInstanceOf(InlineKeyboardMarkup.class, inlineKeyboardMarkup);
        assertEquals(3,inlineKeyboardMarkup.getKeyboard().size());
        for(var row : inlineKeyboardMarkup.getKeyboard()) {
            if(row.get(0).getCallbackData().equals("T+marked")){
                assertTrue(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
                assertTrue(row.get(0).getCallbackData().endsWith("marked"));
            }
            else{
                assertFalse(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
                assertFalse(row.get(0).getCallbackData().endsWith("marked"));}
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"T","T2"})
    void chooseButtonTest(String field) throws NoDivisionsException, NoDivisionKeyboardException {
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(), userFrom.getId());
        //При нажатии на кнопку к тексту прибавляется/убирается галочка, к коллбэку - пометка
        EditMessageReplyMarkup firstPress = divisionKeyboardUtils.divisionChooseField(update, field);
        var firstKeyboard=firstPress.getReplyMarkup().getKeyboard();
        for(var row : firstKeyboard) {
            if(row.get(0).getCallbackData().equals(field)){
                assertTrue(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
                assertTrue(row.get(0).getCallbackData().endsWith("marked"));}
        }
        var secondPress =divisionKeyboardUtils.divisionChooseField(update,field+"+marked");
        var secondKeyboard = secondPress.getReplyMarkup().getKeyboard();
        for(var row : secondKeyboard) {
            if(row.get(0).getCallbackData().equals(field)){
                assertFalse(EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white"));
                assertFalse(row.get(0).getCallbackData().endsWith("marked"));}
        }
    }


    @Test
    void getMarkedButtonsTest() throws NoDivisionsException, NoDivisionKeyboardException {
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(), userFrom.getId());
        //Проверка выдачи кнопок с отметками
        divisionKeyboardUtils.divisionChooseField(update,"T");
        divisionKeyboardUtils.divisionChooseField(update,"T2");
        assertEquals(2,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
        divisionKeyboardUtils.divisionChooseField(update,"T+marked");
        assertEquals(1,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
        divisionKeyboardUtils.divisionChooseField(update,"T2+marked");
        assertEquals(0,divisionKeyboardUtils.getMarkedDivisions(user.getId()).size());
    }

    @Test
    void keyBoardDivsVarsTest() throws NoDivisionsException, NoDivisionKeyboardException {
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(), userFrom.getId());
        //Проверка добавления в мапу всех возможных отделов *2 (с отметками)
        divisionKeyboardUtils.divisionChooseField(update,"T");
        HashMap<String, DivisionModel> keyboardDivs = divisionKeyboardUtils.getMapKeyboardDivisions();
        assertEquals(4,keyboardDivs.size());
    }

    @Test
    void getCachedDivisionsTest() throws NoDivisionsException, NoDivisionKeyboardException {
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(), userFrom.getId());
        divisionKeyboardUtils.getCustomerDivisionKeyboard();
        assertEquals(divisionService.findAll().size(),divisionKeyboardUtils.getCachedDivisions().size());
        divisionKeyboardUtils.divisionChooseField(update,"T");
        assertEquals(divisionService.findAll().size(), divisionKeyboardUtils.getCachedDivisions().size());
    }
    @Test
    void getCachedDivisionsWithEmptyCacheTest(){
        divisionKeyboardUtils.clear(user.getId());
        assertThrows(NoDivisionsException.class,()-> divisionKeyboardUtils.getCachedDivisions());
    }

    @Test
    void clearTest() throws NoDivisionsException {
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(), userFrom.getId());
        divisionKeyboardUtils.clear(user.getId());
        assertThrows(NoDivisionsException.class,()-> divisionKeyboardUtils.getCachedDivisions());
    }

}