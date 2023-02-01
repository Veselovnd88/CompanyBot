package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.ChatService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@ActiveProfiles("test")
public class WorkFlowTest {

    @MockBean
    CompanyBot bot;
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;
    @Autowired
    ChatService chatService;

    @Test
    void workFlowTest() {
        Chat chat = new Chat();
        chat.setId(-100L);
        chat.setTitle("Channel");
        chat.setType("group");
        chatService.save(chat);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int n=50;
        for(int i=0; i<n; i++) {
            User user = new User();
            user.setUserName("Vasya"+i);
            user.setLastName("Petya"+i);
            user.setId(100L+i);
            Runnable task = () -> {
                UserActions userActions = new UserActions();
                assertInstanceOf(SendMessage.class,
                        telegramFacadeUpdateHandler.processUpdate(userActions.userPressStart(user)));
                assertEquals(BotState.READY, userDataCache.getUserBotState(user.getId()));
                assertInstanceOf(SendMessage.class,
                        telegramFacadeUpdateHandler.processUpdate(userActions.userPressInquiry(user)));
                assertEquals(BotState.AWAIT_DIVISION_FOR_INQUIRY, userDataCache.getUserBotState(user.getId()));
                assertInstanceOf(SendMessage.class,
                        telegramFacadeUpdateHandler.processUpdate(userActions.userPressInquiryButton(user)));
                assertEquals(BotState.AWAIT_MESSAGE, userDataCache.getUserBotState(user.getId()));
                for (int i1 = 0; i1 < 5; i1++) {
                    telegramFacadeUpdateHandler.processUpdate(userActions.userSendMessage(user));
                }
                assertEquals(BotState.AWAIT_MESSAGE, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userPressContactButton(user));
                assertEquals(BotState.AWAIT_CONTACT, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userChooseContactButton(user, "phone"));
                assertEquals(BotState.AWAIT_PHONE, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userInputContactData(user, "+89156669009"));
                assertEquals(BotState.AWAIT_CONTACT, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userChooseContactButton(user, "name"));
                assertEquals(BotState.AWAIT_NAME, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userInputContactData(user, "Vasya Petya Valera"));
                assertEquals(BotState.AWAIT_CONTACT, userDataCache.getUserBotState(user.getId()));
                telegramFacadeUpdateHandler.processUpdate(userActions.userChooseContactButton(user, "save"));
                assertEquals(BotState.READY, userDataCache.getUserBotState(user.getId()));
            };
            executorService.execute(task);
        }
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO и такой же тест но с добавлением в чат
}
