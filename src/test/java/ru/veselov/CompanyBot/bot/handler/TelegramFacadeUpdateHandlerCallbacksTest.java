package ru.veselov.CompanyBot.bot.handler;

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
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactMessageHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.DivisionCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.InquiryMessageHandler;
import ru.veselov.CompanyBot.bot.handler.managing.AddDivisionToManagerFromCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.managing.DivisionMenuCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.managing.ManageModeCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.managing.ManagerMenuCallbackHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.BotAnswerUtil;

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
    BotAnswerUtil botAnswerUtil;
    @Autowired
    UserDataCache userDataCache;
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
    void DivisionCallBackHandlerNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(b!=BotState.AWAIT_DIVISION_FOR_INQUIRY){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(divisionCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }
    @Test
    void DivisionCallBachHandlerCallTest(){
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_DIVISION_FOR_INQUIRY);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(divisionCallbackHandler).processUpdate(any(Update.class));

    }

    @Test
    void AddDivisionToManagerNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(b!=BotState.ASSIGN_DIV){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(addDivisionToManagerFromCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }
    @Test
    void AddDivisionToManagerTest(){
        userDataCache.setUserBotState(user.getId(),BotState.ASSIGN_DIV);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(addDivisionToManagerFromCallbackHandler).processUpdate(any(Update.class));
    }

    @Test
    void DivisionMenuNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(b!=BotState.MANAGE_DIVISION){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(divisionMenuCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }
    @Test
    void DivisionMenuCallTest(){
        userDataCache.setUserBotState(user.getId(),BotState.MANAGE_DIVISION);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(divisionMenuCallbackHandler).processUpdate(any(Update.class));
    }

    @Test
    void ManagerMenuNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(b!=BotState.MANAGE_MANAGER){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(managerMenuCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }
    @Test
    void ManagerMenuCallTest(){
        userDataCache.setUserBotState(user.getId(),BotState.MANAGE_MANAGER);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(managerMenuCallbackHandler).processUpdate(any(Update.class));
    }

    @Test
    void ManageMenuNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(b!=BotState.MANAGE){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(manageModeCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    void ManageMenuCallTest(){
        userDataCache.setUserBotState(user.getId(),BotState.MANAGE);
        telegramFacadeUpdateHandler.processUpdate(update);
        verify(manageModeCallbackHandler).processUpdate(any(Update.class));
    }

    @Test
    void ContactCallbackHandlerNoCallsTest(){
        for(var  b: BotState.values()){
            userDataCache.setUserBotState(user.getId(),b);
            if(!isContactInputState(b)){
                telegramFacadeUpdateHandler.processUpdate(update);
                verify(contactCallbackHandler,never()).processUpdate(any(Update.class));
            }
        }
    }

    @Test
    void ContactCallbackHandlerCallTest(){
        List<BotState> states = List.of(BotState.AWAIT_NAME,BotState.AWAIT_SHARED,BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL,BotState.AWAIT_CONTACT);
        for(var b: states){
            userDataCache.setUserBotState(user.getId(),b);
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