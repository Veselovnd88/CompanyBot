package ru.veselov.companybot.bot.handler.callback;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.handler.callback.impl.SaveContactCallbackUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.event.SendCustomerDataEventPublisher;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.InquiryService;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class SaveContactCallbackUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    SendCustomerDataEventPublisher customerDataEventPublisher;

    @Mock
    CustomerService customerService;

    @Mock
    InquiryService inquiryService;

    @Mock
    ContactKeyboardHelperImpl contactKeyboardHelper;

    @Mock
    CallbackQueryDataHandlerContext context;

    @InjectMocks
    SaveContactCallbackUpdateHandlerImpl saveContactCallbackUpdateHandler;

    Update update;
    CallbackQuery callbackQuery;
    User user;

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        callbackQuery = Mockito.spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user = Mockito.spy(User.class);
        user.setId(TestUtils.USER_ID);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
    }

    @Test
    void shouldSaveAndPublishEvenWithoutInquiry() {
        ContactModel contactModel = ContactModel.builder().phone("+7 888 888 88 88").firstName("name").build();
        Mockito.when(userDataCache.getContact(user.getId())).thenReturn(contactModel);

        AnswerCallbackQuery answerCallbackQuery = saveContactCallbackUpdateHandler.processUpdate(update);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).getInquiry(user.getId()),
                () -> Mockito.verify(customerService).saveContact(contactModel),
                () -> Mockito.verify(customerDataEventPublisher).publishEvent(null, contactModel),
                () -> Mockito.verify(contactKeyboardHelper).clear(user.getId()),
                () -> Mockito.verify(userDataCache).clear(user.getId()),
                () -> Assertions.assertThat(answerCallbackQuery.getText()).isEqualTo(MessageUtils.SAVED),
                () -> Mockito.verifyNoInteractions(inquiryService)
        );
    }

    @Test
    void shouldSaveAndPublishEvenWithInquiry() {
        ContactModel contactModel = ContactModel.builder().phone("+7 888 888 88 88").firstName("name").build();
        Mockito.when(userDataCache.getContact(user.getId())).thenReturn(contactModel);
        InquiryModel inquiryModel = new InquiryModel();
        inquiryModel.addMessage(new Message());
        Mockito.when(userDataCache.getInquiry(user.getId())).thenReturn(inquiryModel);
        AnswerCallbackQuery answerCallbackQuery = saveContactCallbackUpdateHandler.processUpdate(update);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(userDataCache).getInquiry(user.getId()),
                () -> Mockito.verify(customerService).saveContact(contactModel),
                () -> Mockito.verify(customerDataEventPublisher).publishEvent(inquiryModel, contactModel),
                () -> Mockito.verify(contactKeyboardHelper).clear(user.getId()),
                () -> Mockito.verify(userDataCache).clear(user.getId()),
                () -> Assertions.assertThat(answerCallbackQuery.getText()).isEqualTo(MessageUtils.SAVED),
                () -> Mockito.verify(inquiryService).save(inquiryModel)
        );
    }

    @Test
    void shouldThrowExceptionForEmptyContact() {
        ContactModel contactModel = new ContactModel();
        Mockito.when(userDataCache.getContact(user.getId())).thenReturn(contactModel);

        Assertions.assertThatThrownBy(() -> saveContactCallbackUpdateHandler.processUpdate(update))
                .isInstanceOf(ContactProcessingException.class);
    }

    @Test
    void shouldRegisterInContext() {
        saveContactCallbackUpdateHandler.registerInContext();

        Mockito.verify(context).add(CallBackButtonUtils.SAVE, saveContactCallbackUpdateHandler);
    }

    @Test
    void shouldReturnAvailableStates() {
        Set<BotState> availableStates = saveContactCallbackUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates)
                .isEqualTo(Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                        BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE));
    }

}
