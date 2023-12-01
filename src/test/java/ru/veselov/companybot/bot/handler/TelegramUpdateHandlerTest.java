package ru.veselov.companybot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.HandlerContext;
import ru.veselov.companybot.cache.UserDataCacheFacade;

import static org.mockito.Mockito.*;

@SpringBootTest
class TelegramUpdateHandlerTest {
    @MockBean
    CompanyBot bot;
    @Autowired
    UserDataCacheFacade userDataCacheFacade;
    Update update;
    CallbackQuery callbackQuery;
    Message message;
    User user;
    @Autowired
    HandlerContext handlerContext;

    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        message=spy(Message.class);
        user = spy(User.class);
        callbackQuery.setFrom(user);
        user.setId(100L);
        update.setCallbackQuery(callbackQuery);
    }


}