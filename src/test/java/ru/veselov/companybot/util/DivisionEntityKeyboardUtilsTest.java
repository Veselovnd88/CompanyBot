package ru.veselov.companybot.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.util.DivisionKeyboardUtils;
import ru.veselov.companybot.exception.NoDivisionsException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DivisionEntityKeyboardUtilsTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    DivisionKeyboardUtils divisionKeyboardUtils;
    @MockBean
    private DivisionServiceImpl divisionService;


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
                DivisionModel.builder().divisionId(UUID.randomUUID()).name("Test").build(),
                DivisionModel.builder().divisionId(UUID.randomUUID()).name("Test2").build()
        ));
    }

    @Test
    void getCachedDivisionsWithEmptyCacheTest(){
        divisionKeyboardUtils.clear(user.getId());
        assertThrows(NoDivisionsException.class,()-> divisionKeyboardUtils.getCachedDivisions());
    }


}