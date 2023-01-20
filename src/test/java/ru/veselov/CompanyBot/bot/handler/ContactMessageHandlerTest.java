package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.util.KeyBoardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ContactMessageHandlerTest {
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private UserDataCache userDataCache;
    @MockBean
    KeyBoardUtils keyBoardUtils;
    @Autowired
    private ContactCache contactCache;
    @Autowired
    ContactMessageHandler contactMessageHandler;
    Update update;
    Message message;
    User user;

    @BeforeEach
    void init(){
        update=spy(Update.class);
        message=spy(Message.class);
        user =spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        message.setEntities(List.of(messageEntity));
        contactCache.createContact(user.getId());
    }


    @Test
    void nameTest(){
        /*Проверка ввода контакта текстом*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_NAME);
        message.setText(" Ivanov Ivan Ivanovich");
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"name");
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"+79175550335","89167861234","8-495-250-23-93","+2 234 345-24-66"})
    void phoneTest(String phone){
        /*Проверка ввода контакта текстом*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_PHONE);
        message.setText(phone);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"phone");
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"+7a9175550335","891","8-495asdf-250-23-93","+99999992 234 345-24-66"})
    void wrongPhoneTest(String phone){
        /*Проверка ввода контакта текстом*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_PHONE);
        message.setText(phone);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils,never()).editMessageSavedField(user.getId(),"phone");
        assertEquals(BotState.AWAIT_PHONE,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"veselovnd@gmail.com","123@123.com","sfd@asdf.ru"})
    void emailTest(String email){
        /*Проверка ввода электронной почты*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText(email);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"email");
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"gmail.com","asdf@","hate@."})
    void wrongEmailTest(String email){
        /*Неправильные адреса*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText(email);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils,never()).editMessageSavedField(user.getId(),"email");
        assertEquals(MessageUtils.WRONG_EMAIL,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_EMAIL,userDataCache.getUserBotState(user.getId()));
    }


    @Test
    void contactTest(){
        /*Проверка ввода контакта*/
        message.setText(null);
        Contact contact= new Contact();
        message.setContact(contact);
        message.setEntities(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"shared");
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @Test
    void noTextTest(){
        /*Проверка когда поступают неправильные данные*/
        message.setText(null);
        message.setContact(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.WRONG_CONTACT_FORMAT,((SendMessage)botApiMethod).getText());
    }

    @Test
    void processNameTestFull(){
        String name = "Pipkov Vasya Petrovich";
        CustomerContact contact = new CustomerContact();
        contactMessageHandler.getProcessedName(contact, name);
        assertEquals("Pipkov",contact.getLastName());
        assertEquals("Vasya",contact.getFirstName());
        assertEquals("Petrovich",contact.getSecondName());
    }

    @Test
    void processNameTestOnlyLastName(){
        String name = "Pipkov";
        CustomerContact contact = new CustomerContact();
        contactMessageHandler.getProcessedName(contact, name);
        assertEquals("Pipkov",contact.getLastName());
    }
    @Test
    void processNameTestOnlyFirstLast(){
        String name = "Pipkov Ivan";
        CustomerContact contact = new CustomerContact();
        contactMessageHandler.getProcessedName(contact, name);
        assertEquals("Pipkov",contact.getLastName());
        assertEquals("Ivan",contact.getFirstName());
    }

    @Test
    void processNameTestMoreThanThreeParts(){
        String name = "Pipkov Ivan Petrovich Vasiliy Evil";
        CustomerContact contact = new CustomerContact();
        contactMessageHandler.getProcessedName(contact, name);
        assertEquals("Pipkov",contact.getLastName());
        assertEquals("Ivan",contact.getFirstName());
        assertEquals("Petrovich Vasiliy Evil",contact.getSecondName());
    }
    @ParameterizedTest
    @ValueSource(strings = {""," "})
    void processNameTestWithIncorrectName(String name){
        CustomerContact contact = new CustomerContact();
        contact.setUserId(100L);
        contactMessageHandler.getProcessedName(contact, name);
        assertNull(contact.getLastName());
    }

}