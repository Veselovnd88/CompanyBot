package ru.veselov.companybot.bot.handler.inquiry;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.impl.InquiryServiceImpl;
import ru.veselov.companybot.service.impl.SenderService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ContactCallbackHandlerTest {

    @MockBean
    CompanyBot bot;

    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ContactCache contactCache;
    @Autowired
    ContactCallbackHandler contactCallbackHandler;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private InquiryServiceImpl inquiryService;
    @MockBean
    SenderService senderService;

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
    @SneakyThrows
    void contactDataTest(){
        /*Проверка смены статуса при нажатии кнопки контакт*/
        callbackQuery.setData("contact");
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    @SneakyThrows
    void repeatDataTest(){
        callbackQuery.setData("repeat");
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
    }
    @Test
    @SneakyThrows
    void saveWithInquiryDataTest() {
        /*Checking saving inquiry and contact together*/
        callbackQuery.setData("save");
        contactCache.createContact(user.getId());
        ContactModel contact = contactCache.getContact(user.getId());
        contact.setEmail("vasya@vasya.ru");
        contact.setLastName("Petrov");
        userDataCache.createInquiry(user.getId(), DivisionModel.builder().divisionId("L").build());
        InquiryModel inquiry = userDataCache.getInquiry(user.getId());
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
        verify(customerService).saveContact(contact);
        verify(inquiryService).save(inquiry);
        assertNull(contactCache.getContact(user.getId()));
        assertNull(userDataCache.getInquiry(user.getId()));
        verify(senderService).send(inquiry,contact);
    }
    @Test
    @SneakyThrows
    void saveWithoutInquiryDataTest() {
        /*Checking saving contact without inquiry*/
        callbackQuery.setData("save");
        contactCache.createContact(user.getId());
        ContactModel contact = contactCache.getContact(user.getId());
        contact.setEmail("vasya@vasya.ru");
        contact.setLastName("Petrov");
        InquiryModel inquiry = userDataCache.getInquiry(user.getId());
        assertNotNull(contactCallbackHandler.processUpdate(update));
        assertEquals(BotState.READY,userDataCache.getUserBotState(user.getId()));
        verify(customerService).saveContact(contact);
        verify(inquiryService,times(0)).save(inquiry);
        assertNull(contactCache.getContact(user.getId()));
        assertNull(userDataCache.getInquiry(user.getId()));
        verify(senderService).send(inquiry,contact);
    }

    @Test
    void notCorrectData(){
        /*Не корректные данные с коллбэка*/
        callbackQuery.setData("unknown");
        assertThrows(NoAvailableActionCallbackException.class, ()->contactCallbackHandler.processUpdate(update));
    }


}