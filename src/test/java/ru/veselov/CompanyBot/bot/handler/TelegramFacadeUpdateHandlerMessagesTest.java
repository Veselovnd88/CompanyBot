package ru.veselov.CompanyBot.bot.handler;

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
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactMessageHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.DivisionCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.InquiryMessageHandler;
import ru.veselov.CompanyBot.bot.handler.managing.*;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;

import java.util.List;

import static org.junit.jupiter.api.AssertThrows.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TelegramFacadeUpdateHandlerMessagesTest {
    @MockBean
    CompanyBot companyBot;
    @Value("${bot.adminId}")
    private String adminId;
    @MockBean
    DivisionCallbackHandler divisionCallbackHandler;
    @MockBean
    ContactCallbackHandler contactCallbackHandler;
    @MockBean
    ContactMessageHandler contactMessageHandler;
    @MockBean
    InquiryMessageHandler inquiryMessageHandler;
    @MockBean
    ManageModeCallbackHandler manageModeCallbackHandler;
    @MockBean
    ManagerMenuCallbackHandler managerMenuCallbackHandler;
    @MockBean
    DivisionMenuCallbackHandler divisionMenuCallbackHandler;
    @MockBean
    AddDivisionToManagerFromCallbackHandler addDivisionToManagerFromCallbackHandler;
    @MockBean
    AddDivisionTextMessageHandler addDivisionTextMessageHandler;
    @MockBean
    AddManagerFromForwardMessageHandler addManagerFromForwardMessageHandler;
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    Update update;
    User user;
    Message message;


    @BeforeEach
    void init() {
        update = spy(Update.class);
        user = spy(User.class);
        message = spy(Message.class);
        user.setId(100L);
        update.setMessage(message);
        message.setFrom(user);
    }

    @Test
    @SneakyThrows
    void addManagerFromForwardMessageHandlerNoCallsTest() {
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
            if (b != BotState.AWAIT_MANAGER&&b!=BotState.DELETE_MANAGER) {
                assertThrows(NoAvailableActionSendMessageException.class,
                        ()->telegramFacadeUpdateHandler.processUpdate(update) );
                verify(addManagerFromForwardMessageHandler, never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    @SneakyThrows
    void addManagerFromForwardMessageHandlerCallTest() {
        userDataCache.setUserBotState(user.getId(), BotState.AWAIT_MANAGER);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(addManagerFromForwardMessageHandler).processUpdate(any(Update.class));
        userDataCache.setUserBotState(user.getId(), BotState.DELETE_MANAGER);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(addManagerFromForwardMessageHandler, times(2)).processUpdate(any(Update.class));
    }
    @Test
    @SneakyThrows
    void addDivisionTextMessageHandlerNoCallsTest() {
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
            if (b != BotState.AWAIT_DIVISION) {
                assertThrows(NoAvailableActionSendMessageException.class,
                        ()->telegramFacadeUpdateHandler.processUpdate(update);)
                verify(addDivisionTextMessageHandler, never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    @SneakyThrows
    void addDivisionTextMessageHandlerCallTest() {
        userDataCache.setUserBotState(user.getId(), BotState.AWAIT_DIVISION);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(addDivisionTextMessageHandler).processUpdate(any(Update.class));
    }

    @Test
    @SneakyThrows
    void contactNoCallsTest() {
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
            if (!isContactInputState(b)) {
                assertThrows(NoAvailableActionSendMessageException.class,
                        ()->telegramFacadeUpdateHandler.processUpdate(update); )
                verify(contactMessageHandler, never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    @SneakyThrows
    void contactCallTest() {
        List<BotState> states = List.of(BotState.AWAIT_NAME, BotState.AWAIT_SHARED, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_CONTACT);
        for(var b: states){
            userDataCache.setUserBotState(user.getId(), b);
            telegramFacadeUpdateHandler.processUpdate(update);
        }
        verify(contactMessageHandler, times(states.size())).processUpdate(any(Update.class));
    }

    @Test
    @SneakyThrows
    void inquiryNoCallsTest() {
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
            if (b!=BotState.AWAIT_MESSAGE) {
                assertThrows(NoAvailableActionSendMessageException.class,
                        ()->telegramFacadeUpdateHandler.processUpdate(update);)
                verify(inquiryMessageHandler, never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    @SneakyThrows
    void inquiryCallTest() {
        userDataCache.setUserBotState(user.getId(), BotState.AWAIT_MESSAGE);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(inquiryMessageHandler).processUpdate(any(Update.class));
    }

    private boolean isContactInputState(BotState botState) {
        List<BotState> states = List.of(BotState.AWAIT_NAME, BotState.AWAIT_SHARED, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_CONTACT);
        return states.contains(botState);
    }
}