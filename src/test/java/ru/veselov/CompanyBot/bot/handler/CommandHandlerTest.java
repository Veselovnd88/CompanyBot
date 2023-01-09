package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.util.MessageUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CommandHandlerTest {
    @MockBean
    private CompanyBot companyBot;
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    CommandHandler commandHandler;
    @MockBean
    private CustomerService customerService;

    Update update= spy(Update.class);
    User user= spy(User.class);
    Message message=spy(Message.class);
    @BeforeEach
    void init(){
        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(100L);
    }

    @Test
    void startCommandNoStateTest(){
        /*Проверка входа в case с отсутствием статуса */
        userDataCache.setUserBotState(100L, null);
        when(message.getText()).thenReturn("/start");
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        verify(customerService,times(1)).save(user);
        assertEquals(MessageUtils.GREETINGS,((SendMessage) botApiMethod).getText());
        assertEquals(BotState.READY,userDataCache.getUserBotState(100L));
    }

    @Test
    void startCommandWithStateTest(){
        /*Проверка входа в case с любого статуса бота, кроме BEGIN*/
        when(message.getText()).thenReturn("/start");
        for(BotState b: BotState.values()){
            if(b!=BotState.BEGIN){
                userDataCache.setUserBotState(100L, b);
                BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
                verify(customerService,times(0)).save(user);
                assertEquals(MessageUtils.GREETINGS,((SendMessage) botApiMethod).getText());
                assertEquals(BotState.READY,userDataCache.getUserBotState(100L));
                assertNull(userDataCache.getInquiry(100L));
            }
        }
    }

    @Test
    void inquiryCommandWithStateTest(){
        /*Проверка входа в case с правильным статусом*/
        when(message.getText()).thenReturn("/inquiry");
        userDataCache.setUserBotState(100L,BotState.READY);
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        assertEquals(MessageUtils.CHOOSE_DEP,((SendMessage) botApiMethod).getText());
        assertEquals(BotState.AWAIT_DEPARTMENT,userDataCache.getUserBotState(100L));
    }

    @Test
    void inquiryCommandWithWrongStateTest(){
        /*Проверка входа в case с неправильным статусом*/
        when(message.getText()).thenReturn("/inquiry");
        for(var b: BotState.values()){
            if(b!=BotState.READY){
                userDataCache.setUserBotState(100L,b);
                BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
                assertEquals(MessageUtils.NOT_READY,((SendMessage) botApiMethod).getText());
                assertEquals(b,userDataCache.getUserBotState(100L));
            }
        }
    }

    @Test
    void aboutCommandTest(){
        /*Проверка входа в case с любым статусом*/
        when(message.getText()).thenReturn("/about");
        for(var b: BotState.values()){
            userDataCache.setUserBotState(100L,b);
            BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
            assertEquals(MessageUtils.ABOUT,((SendMessage) botApiMethod).getText());
            assertEquals(b,userDataCache.getUserBotState(100L));
        }
    }

    @Test
    void infoCommandTest(){
        /*Проверка входа в case с любым статусом*/
        when(message.getText()).thenReturn("/info");
        for(var b: BotState.values()){
            userDataCache.setUserBotState(100L,b);
            BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
            assertEquals(MessageUtils.INFO,((SendMessage) botApiMethod).getText());
            assertEquals(b,userDataCache.getUserBotState(100L));
        }
    }

    @Test
    void wrongCommandTest(){
        /*Проверка работы при подаче любой неправильной команды*/
        when(message.getText()).thenReturn("/anyCommand");
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        assertEquals(MessageUtils.UNKNOWN_COMMAND,((SendMessage) botApiMethod).getText());
    }
}