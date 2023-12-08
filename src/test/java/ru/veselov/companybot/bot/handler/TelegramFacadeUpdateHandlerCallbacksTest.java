package ru.veselov.companybot.bot.handler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.HandlerContext;
import ru.veselov.companybot.bot.handler.callback.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.handler.callback.impl.DivisionCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.impl.ContactMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TelegramFacadeUpdateHandlerCallbacksTest {
    @MockBean
    CompanyBot companyBot;
    @Value("${bot.adminId}")
    private String adminId;
    @MockBean
    DivisionCallbackUpdateHandlerImpl divisionCallbackHandler;
    @MockBean
    ContactCallbackUpdateHandler contactCallbackHandler;
    @MockBean
    ContactMessageUpdateHandlerImpl contactMessageHandler;
    @MockBean
    InquiryMessageUpdateHandlerImpl inquiryMessageHandler;

    @Autowired
    HandlerContext handlerContext;

    @Autowired
    UserDataCacheFacade userDataCacheFacade;
    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    Update update;
    CallbackQuery callbackQuery;
    User user;


    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery = spy(CallbackQuery.class);
        user = spy(User.class);
        user.setId(100L);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setFrom(user);
    }

    @Test
    @SneakyThrows
    void DivisionCallBackHandlerNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCacheFacade.setUserBotState(user.getId(),b);
            if(handlerContext.isInCallbackContext(b)){
                if(b!=BotState.AWAIT_DIVISION_FOR_INQUIRY){
                    telegramFacadeUpdateHandler.processUpdate(update);
                    verify(divisionCallbackHandler,never()).processUpdate(any(Update.class));
                }
            }
        }
    }
    @Test
    @SneakyThrows
    void DivisionCallBachHandlerCallTest(){
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_DIVISION_FOR_INQUIRY);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(divisionCallbackHandler).processUpdate(any(Update.class));

    }

        @Test
    @SneakyThrows
    void ContactCallbackHandlerNoCallsTest(){
        for(var  b: BotState.values()){
            if(handlerContext.isInCallbackContext(b)){
                userDataCacheFacade.setUserBotState(user.getId(),b);
                if(!isContactInputState(b)&& b!=BotState.AWAIT_MESSAGE){
                    telegramFacadeUpdateHandler.processUpdate(update);
                    verify(contactCallbackHandler,never()).processUpdate(any(Update.class));
                }
            }
        }
    }

    @Test
    @SneakyThrows
    void ContactCallbackHandlerCallTest(){
        List<BotState> states = List.of(BotState.AWAIT_NAME,BotState.AWAIT_SHARED,BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL,BotState.AWAIT_CONTACT);
        for(var b: states){
            userDataCacheFacade.setUserBotState(user.getId(),b);
            telegramFacadeUpdateHandler.processUpdate(update);
        }

        verify(contactCallbackHandler,times(states.size())).processUpdate(any(Update.class));
    }

    private boolean isContactInputState(BotState botState){
        List<BotState> states = List.of(BotState.AWAIT_NAME,BotState.AWAIT_SHARED,BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL,BotState.AWAIT_CONTACT);
        return states.contains(botState);
    }

}