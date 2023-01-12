package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.model.Department;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.service.InquiryService;
import ru.veselov.CompanyBot.util.MessageUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ContactCallbackHandlerTest {

    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ContactCache contactCache;
    @Autowired
    ContactCallbackHandler contactCallbackHandler;
    @MockBean
    private  CustomerService customerService;
    @MockBean
    private InquiryService inquiryService;

    Update update;
    CallbackQuery callbackQuery;
    User user;
    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(100L);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @Test
    void contactDataTest(){
        /*Проверка смены статуса при нажатии кнопки контакт*/
        callbackQuery.setData("contact");
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    void repeatDataTest(){
        callbackQuery.setData("repeat");
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    void saveWithInquiryDataTest(){
        /*Проверка сохранения контакта и запроса*/
        callbackQuery.setData("save");
        CustomerContact contact = CustomerContact.builder().userId(user.getId()).email("vasya").build();
        contactCache.addContact(user.getId(),contact);
        userDataCache.createInquiry(user.getId(), Department.COMMON);
        CustomerInquiry inquiry = userDataCache.getInquiry(user.getId());
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
        verify(customerService).saveContact(contact);
        verify(inquiryService).save(inquiry);
        assertNull(contactCache.getContact(user.getId()));
        assertNull(userDataCache.getInquiry(user.getId()));
        //TODO проверка вызова сервиса для отправки в чаты
    }
    @Test
    void saveWithoutInquiryDataTest(){
        /*Проверка сохранения без запроса*/
        callbackQuery.setData("save");
        CustomerContact contact = CustomerContact.builder().userId(user.getId()).email("vasya").build();
        contactCache.addContact(user.getId(),contact);
        CustomerInquiry inquiry = userDataCache.getInquiry(user.getId());
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
        verify(customerService).saveContact(contact);
        verify(inquiryService,times(0)).save(inquiry);
        assertNull(contactCache.getContact(user.getId()));
        assertNull(userDataCache.getInquiry(user.getId()));
        //TODO проверка вызова сервиса для отправки в чаты
    }

    @Test
    void notCorrectData(){
        /*Не корректные данные с коллбэка*/
        callbackQuery.setData("unknown");
        BotApiMethod<?> botApiMethod = contactCallbackHandler.processUpdate(update);
        assertEquals(MessageUtils.ERROR,((AnswerCallbackQuery) botApiMethod).getText());
    }


}