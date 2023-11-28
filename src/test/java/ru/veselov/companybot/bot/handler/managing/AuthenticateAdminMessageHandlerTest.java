package ru.veselov.companybot.bot.handler.managing;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.cache.UserDataCache;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@ActiveProfiles("test")
class AuthenticateAdminMessageHandlerTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    AuthenticateAdminMessageHandler authenticateAdminMessageHandler;

    @Autowired
    UserDataCache userDataCache;

    @Value("${bot.admin_pass}")
    private String pass;

    Update update;
    Message message;

    User user;

    @BeforeEach
    void init(){
        update=new Update();
        message=new Message();
        update.setMessage(message);
        user = new User();
        user.setId(100L);
        message.setFrom(user);
        userDataCache.setUserBotState(user.getId(),BotState.READY);
        authenticateAdminMessageHandler.setPass(pass);
    }



    @Test
    @SneakyThrows
    void passwordTest(){
        message.setText("password");
        authenticateAdminMessageHandler.processUpdate(update);
        assertEquals(BotState.MANAGE, userDataCache.getUserBotState(user.getId()));
    }

    @Test
    @SneakyThrows
    void wrongPasswordTest(){
        message.setText("password!!!");
        authenticateAdminMessageHandler.processUpdate(update);
        assertEquals(BotState.READY, userDataCache.getUserBotState(user.getId()));
    }





}