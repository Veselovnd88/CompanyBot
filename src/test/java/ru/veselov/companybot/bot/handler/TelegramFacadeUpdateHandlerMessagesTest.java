package ru.veselov.companybot.bot.handler;

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
import ru.veselov.companybot.bot.HandlerContext;
import ru.veselov.companybot.bot.handler.inquiry.ContactCallbackHandler;
import ru.veselov.companybot.bot.handler.inquiry.impl.ContactMessageHandlerImpl;
import ru.veselov.companybot.bot.handler.inquiry.DivisionCallbackHandler;
import ru.veselov.companybot.bot.handler.inquiry.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    ContactMessageHandlerImpl contactMessageHandler;
    @MockBean
    InquiryMessageUpdateHandlerImpl inquiryMessageHandler;
    @MockBean

    @Autowired
    UserDataCacheFacade userDataCacheFacade;
    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;
    @Autowired
    HandlerContext handlerContext;

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
    void contactNoCallsTest() {
        for (var b : BotState.values()) {
            if(handlerContext.isInMessageContext(b)) {
                userDataCacheFacade.setUserBotState(user.getId(), b);
                if (!isContactInputState(b)) {
                    telegramFacadeUpdateHandler.processUpdate(update);
                    verify(contactMessageHandler, never()).processUpdate(any(Update.class));
                }
            }
        }
    }

    @Test
    @SneakyThrows
    void contactCallTest() {
        List<BotState> states = List.of(BotState.AWAIT_NAME, BotState.AWAIT_SHARED, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_CONTACT);
        for(var b: states){
            userDataCacheFacade.setUserBotState(user.getId(), b);
            telegramFacadeUpdateHandler.processUpdate(update);
        }
        verify(contactMessageHandler, times(states.size())).processUpdate(any(Update.class));
    }

    @Test
    @SneakyThrows
    void inquiryNoCallsTest() {
        for (var b : BotState.values()) {
            if(handlerContext.isInMessageContext(b)){
                userDataCacheFacade.setUserBotState(user.getId(), b);
                if (b!=BotState.AWAIT_MESSAGE) {
                    telegramFacadeUpdateHandler.processUpdate(update);
                    verify(inquiryMessageHandler, never()).processUpdate(any(Update.class));
                }
            }
        }
    }

    @Test
    @SneakyThrows
    void inquiryCallTest() {
        userDataCacheFacade.setUserBotState(user.getId(), BotState.AWAIT_MESSAGE);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(inquiryMessageHandler).processUpdate(any(Update.class));
    }

    private boolean isContactInputState(BotState botState) {
        List<BotState> states = List.of(BotState.AWAIT_NAME, BotState.AWAIT_SHARED, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_CONTACT);
        return states.contains(botState);
    }
}