package ru.veselov.companybot.bot.handler.inquiry;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.impl.ContactMessageHandlerImpl;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.exception.WrongContactException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ContactMessageHandlerTest {

    @Mock
    UserDataCacheFacade userDataCacheFacade;

    @Mock
    ContactCache contactCache;

    @Mock
    KeyBoardUtils keyBoardUtils;

    @Mock
    ContactMessageProcessor contactMessageProcessor;

    @InjectMocks
    ContactMessageHandlerImpl contactMessageHandler;

    Update update;
    Message message;
    User user;

    ContactModel contact = ContactModel.builder().userId(TestUtils.USER_ID).build();

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        message = Mockito.spy(Message.class);
        user = Mockito.spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(TestUtils.USER_ID);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        message.setEntities(List.of(messageEntity));
        Mockito.when(contactCache.getContact(TestUtils.USER_ID)).thenReturn(contact);
    }


    @Test
    void shouldHandleContactUpdateWithTextName() {
        String name = "name name name";
        message.setText(name);
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.AWAIT_NAME);
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processName(contact, name);
    }

    @Test
    void shouldHandleContactUpdateWithTextPhone() {
        String phone = "+7 916 788 88 88";
        message.setText(phone);
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.AWAIT_PHONE);
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processPhone(contact, phone);
    }

    @Test
    void shouldHandleContactUpdateWithTextEmail() {
        String email = "123@123.com";
        message.setText(email);
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.AWAIT_EMAIL);
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processEmail(contact, email);
    }

    @Test
    void shouldThrowExceptionIfNotSuitableBotState() {
        String name = "name name name";
        message.setText(name);
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.READY);
        Assertions.assertThatThrownBy(() -> contactMessageHandler.processUpdate(update))
                .isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @Test
    void shouldHandleContactUpdateWithSharedContact() {
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.AWAIT_SHARED);
        message.setText(null);
        Contact shared = new Contact();
        message.setContact(shared);
        message.setEntities(null);

        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processSharedContact(contact, shared);
    }

    @Test
    void noTextTest() {
        /*Проверка когда поступают неправильные данные*/
        message.setText(null);
        message.setContact(null);
        assertThrows(WrongContactException.class,
                () -> contactMessageHandler.processUpdate(update));
    }


}