package ru.veselov.CompanyBot.bot.handler.managing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.managing.ManageCallbackHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.MessageUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class ManageCallbackHandlerTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ManageCallbackHandler manageCallbackHandler;

    Update update;
    CallbackQuery callbackQuery;
    User user;
    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(100L);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @Test
    void enteringTest(){
        callbackQuery.setData("managers");
        assertInstanceOf(SendMessage.class,manageCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE_MANAGER,userDataCache.getUserBotState(user.getId()));
        callbackQuery.setData("divisions");
        assertInstanceOf(SendMessage.class,manageCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE_DIVISION,userDataCache.getUserBotState(user.getId()));
        callbackQuery.setData("about");
        assertInstanceOf(SendMessage.class,manageCallbackHandler.processUpdate(update));
        assertEquals(BotState.MANAGE_ABOUT,userDataCache.getUserBotState(user.getId()));
        callbackQuery.setData("exit");
        assertInstanceOf(SendMessage.class,manageCallbackHandler.processUpdate(update));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    void notCorrectData(){
        /*Не корректные данные с коллбэка*/
        callbackQuery.setData("unknown");
        BotApiMethod<?> botApiMethod = manageCallbackHandler.processUpdate(update);
        assertEquals(MessageUtils.ERROR,((AnswerCallbackQuery) botApiMethod).getText());
    }
}