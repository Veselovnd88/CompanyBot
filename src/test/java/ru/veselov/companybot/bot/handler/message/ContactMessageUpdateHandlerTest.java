package ru.veselov.companybot.bot.handler.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.message.impl.ContactMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.exception.WrongContactException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class ContactMessageUpdateHandlerTest {

    @Mock
    UserDataCacheFacade userDataCacheFacade;

    @Mock
    ContactCache contactCache;

    @Mock
    ContactMessageProcessor contactMessageProcessor;

    @InjectMocks
    ContactMessageUpdateHandlerImpl contactMessageHandler;

    Long userId = TestUtils.getSimpleUser().getId();

    ContactModel contact = ContactModel.builder().userId(TestUtils.USER_ID).build();

    @BeforeEach
    void init() {
        Mockito.when(contactCache.getContact(userId)).thenReturn(contact);
    }


    @Test
    void shouldHandleContactUpdateWithTextNamePhoneEmailShared() {
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.AWAIT_NAME);
        Update update = TestUpdates.getUpdateWithMessageWithContactDataByUser("name name name");
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processName(contact, update.getMessage().getText());
    }

    @Test
    void shouldHandleContactUpdateWithTextPhone() {
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.AWAIT_PHONE);
        Update update = TestUpdates.getUpdateWithMessageWithContactDataByUser("+7 916 555 55 55");
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processPhone(contact, update.getMessage().getText());
    }

    @Test
    void shouldHandleContactUpdateWithTextEmail() {
        Update update = TestUpdates.getUpdateWithMessageWithContactDataByUser("123@123.com");
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.AWAIT_EMAIL);
        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processEmail(contact, update.getMessage().getText());
    }

    @Test
    void shouldThrowExceptionIfNotSuitableBotState() {
        Update update = TestUpdates.getUpdateWithMessageWithContactDataByUser("+7 916 555 55 55");
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.READY);
        Assertions.assertThatThrownBy(() -> contactMessageHandler.processUpdate(update))
                .isInstanceOf(WrongBotStateException.class);
    }

    @Test
    void shouldHandleContactUpdateWithSharedContact() {
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.AWAIT_SHARED);
        Update update = TestUpdates.getUpdateWithMessageWithSharedContactByUser(TestUtils.getUserContact());

        contactMessageHandler.processUpdate(update);

        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.USER_ID, BotState.AWAIT_CONTACT);
        Mockito.verify(contactMessageProcessor).processSharedContact(contact, update.getMessage().getContact());
    }

    @Test
    void shouldThrowExceptionIfMessageHasNotTextOrSharedContact() {
        Update update = TestUpdates.getUpdateWithMessageWithSharedContactByUser(TestUtils.getUserContact());
        update.getMessage().setText(null);
        update.getMessage().setContact(null);

        Assertions.assertThatThrownBy(() -> contactMessageHandler.processUpdate(update))
                .isInstanceOf(WrongContactException.class);
    }

    @Test
    void shouldThrowExceptionIfWrongBotStateForSharedContactProcessing() {
        Mockito.when(userDataCacheFacade.getUserBotState(TestUtils.USER_ID))
                .thenReturn(BotState.READY);
        Update update = TestUpdates.getUpdateWithMessageWithSharedContactByUser(TestUtils.getUserContact());

        Assertions.assertThatThrownBy(() -> contactMessageHandler.processUpdate(update))
                .isInstanceOf(WrongBotStateException.class);
    }

}
