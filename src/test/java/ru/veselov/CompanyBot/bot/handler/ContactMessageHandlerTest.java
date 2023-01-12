package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class ContactMessageHandlerTest {
    @Autowired
    private UserDataCache userDataCache;
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
        assertEquals(MessageUtils.INPUT_CONTACT,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
        //FIXME добавить еще проверки

    }
    @Test
    void phoneTest(){
        /*Проверка ввода контакта текстом*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_PHONE);
        message.setText("+7 916 555 88 33");
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.INPUT_CONTACT,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @Test
    void emailTest(){//FIXME parametrized test
        /*Проверка ввода электронной почты*/
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText("veselovnd@gmail.com");
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.INPUT_CONTACT,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }
    @Test
    void wrongEmailTest(){//FIXME parametrized test
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_EMAIL);
        message.setText("veselovnd-gmail.com");
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.WRONG_EMAIL,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_CONTACT,userDataCache.getUserBotState(user.getId()));

    }


    @Test
    void textNoEntitiesTest(){
        /*Проверка ввода контакта, если нет сущностей текста*/
        message.setText("Text");
        message.setEntities(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.SAVE_MESSAGE,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_SAVING,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @Test
    void contactTest(){
        /*Проверка ввода контакта*/
        message.setText(null);
        Contact contact= new Contact();
        message.setContact(contact);
        message.setEntities(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.SAVE_MESSAGE,((SendMessage)botApiMethod).getText());
        assertEquals(BotState.AWAIT_SAVING,userDataCache.getUserBotState(user.getId()));
        assertNotNull(contactCache.getContact(user.getId()));
    }

    @Test
    void noTextTest(){
        /*Проверка когда поступают неправильные данные*/
        message.setText(null);
        message.setContact(null);
        BotApiMethod<?> botApiMethod = contactMessageHandler.processUpdate(update);
        assertEquals(MessageUtils.WRONG_CONTACT_FORMAT,((SendMessage)botApiMethod).getText());
        assertNull(contactCache.getContact(user.getId()));
    }

}