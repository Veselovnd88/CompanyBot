package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.HandlerContext;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TelegramUpdateHandlerTest {
    @MockBean
    CompanyBot bot;
    @Autowired
    UserDataCache userDataCache;
    Update update;
    CallbackQuery callbackQuery;
    Message message;
    User user;
    @Autowired
    HandlerContext handlerContext;

    @Autowired
    TelegramUpdateHandler telegramUpdateHandler;

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