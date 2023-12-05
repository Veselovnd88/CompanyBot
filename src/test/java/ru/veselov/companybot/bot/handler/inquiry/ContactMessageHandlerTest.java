package ru.veselov.companybot.bot.handler.inquiry;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.inquiry.impl.ContactMessageHandlerImpl;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.WrongContactException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class ContactMessageHandlerTest {
    @MockBean
    CommandLineRunner commandLineRunner;
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private UserDataCacheFacade userDataCacheFacade;
    @MockBean
    KeyBoardUtils keyBoardUtils;
    @Autowired
    private ContactCache contactCache;
    @Autowired
    ContactMessageHandlerImpl contactMessageHandler;
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
    @SneakyThrows
    void nameTest(){
        /*Проверка ввода контакта текстом*/
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_NAME);
        message.setText(" Ivanov Ivan Ivanovich");
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"name");
        assertEquals(BotState.AWAIT_CONTACT, userDataCacheFacade.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"+79175550335","89167861234","8-495-250-23-93","+2 234 345-24-66"})
    @SneakyThrows
    void phoneTest(String phone){
        /*Проверка ввода контакта текстом*/
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_PHONE);
        message.setText(phone);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"phone");
        assertEquals(BotState.AWAIT_CONTACT, userDataCacheFacade.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"+7a9175550335","891","8-495asdf-250-23-93","+99999992 234 345-24-66"})
    @SneakyThrows
    void wrongPhoneTest(String phone){
        /*Проверка ввода контакта текстом*/
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_PHONE);
        message.setText(phone);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils,never()).editMessageSavedField(user.getId(),"phone");
        assertEquals(BotState.AWAIT_PHONE, userDataCacheFacade.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"veselovnd@gmail.com","123@123.com","sfd@asdf.ru"})
    @SneakyThrows
    void emailTest(String email){
        /*Проверка ввода электронной почты*/
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText(email);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"email");
        assertEquals(BotState.AWAIT_CONTACT, userDataCacheFacade.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"gmail.com","asdf@","hate@."})
    @SneakyThrows
    void wrongEmailTest(String email){
        /*Неправильные адреса*/
        userDataCacheFacade.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText(email);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils,never()).editMessageSavedField(user.getId(),"email");
        assertEquals(MessageUtils.WRONG_EMAIL,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_EMAIL, userDataCacheFacade.getUserBotState(user.getId()));
    }


    @Test
    @SneakyThrows
    void contactTest(){
        /*Проверка ввода контакта*/
        message.setText(null);
        Contact contact= new Contact();
        message.setContact(contact);
        message.setEntities(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        verify(keyBoardUtils).editMessageSavedField(user.getId(),"shared");
        assertEquals(BotState.AWAIT_CONTACT, userDataCacheFacade.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @Test
    void noTextTest(){
        /*Проверка когда поступают неправильные данные*/
        message.setText(null);
        message.setContact(null);
        assertThrows(WrongContactException.class,
                ()-> contactMessageHandler.processUpdate(update));
    }


}