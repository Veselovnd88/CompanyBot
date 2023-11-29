package ru.veselov.companybot.bot.handler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.util.MessageUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CommandHandlerTest {
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    CommandHandler commandHandler;
    @MockBean
    private CustomerService customerService;
    Update update;
    User user;
    Message message;
    @BeforeEach
    void init(){
        update= spy(Update.class);
        message=spy(Message.class);
        user = spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
    }

    @Test
    @SneakyThrows
    void startCommandNoStateTest(){
        /*Проверка входа в case с отсутствием статуса */
        userDataCache.setUserBotState(user.getId(), null);
        when(message.getText()).thenReturn("/start");
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        verify(customerService,times(1)).save(user);
        assertEquals(MessageUtils.GREETINGS,((SendMessage) botApiMethod).getText());
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    @SneakyThrows
    void startCommandWithStateTest(){
        /*Проверка входа в case с любого статуса бота, кроме BEGIN*/
        when(message.getText()).thenReturn("/start");
        for(BotState b: BotState.values()){
            if(b!=BotState.BEGIN){
                userDataCache.setUserBotState(user.getId(), b);
                BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
                verify(customerService,times(0)).save(user);
                clearInvocations(customerService);
                assertEquals(MessageUtils.GREETINGS,((SendMessage) botApiMethod).getText());
                assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
                assertNull(userDataCache.getInquiry(user.getId()));
            }
        }
    }

    @Test
    @SneakyThrows
    void inquiryCommandWithStateTest(){
        /*Проверка входа в case с правильным статусом*/
        when(message.getText()).thenReturn("/inquiry");
        userDataCache.setUserBotState(user.getId(), BotState.READY);
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        assertEquals(MessageUtils.CHOOSE_DEP,((SendMessage) botApiMethod).getText());
        assertEquals(BotState.AWAIT_DIVISION_FOR_INQUIRY,userDataCache.getUserBotState(user.getId()));
    }

    @Test
    @SneakyThrows
    void inquiryCommandWithWrongStateTest(){
        /*Проверка входа в case с неправильным статусом*/
        when(message.getText()).thenReturn("/inquiry");
        for(var b: BotState.values()){
            if(b!=BotState.READY){
                userDataCache.setUserBotState(user.getId(), b);
                assertThrows(NoAvailableActionSendMessageException.class,
                        ()-> commandHandler.processUpdate(update));
                assertEquals(b,userDataCache.getUserBotState(user.getId()));
            }
        }
    }

    @Test
    @SneakyThrows
    void aboutCommandTest(){
        /*Проверка входа в case с любым статусом*/
        when(message.getText()).thenReturn("/about");
        for(var b: BotState.values()){
            userDataCache.setUserBotState(user.getId(), b);
            assertInstanceOf(SendMessage.class,commandHandler.processUpdate(update));
            assertEquals(b,userDataCache.getUserBotState(user.getId()));
        }
    }

    @Test
    @SneakyThrows
    void infoCommandTest(){
        /*Проверка входа в case с любым статусом*/
        when(message.getText()).thenReturn("/info");
        for(var b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
            assertEquals(MessageUtils.INFO,((SendMessage) botApiMethod).getText());
            assertEquals(b,userDataCache.getUserBotState(user.getId()));
        }
    }

    @Test
    void wrongCommandTest(){
        /*Проверка работы при подаче любой неправильной команды*/
        when(message.getText()).thenReturn("/anyCommand");
        assertThrows(NoAvailableActionSendMessageException.class,
                ()->commandHandler.processUpdate(update));
    }
}