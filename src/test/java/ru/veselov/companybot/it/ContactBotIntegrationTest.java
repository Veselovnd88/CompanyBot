package ru.veselov.companybot.it;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.util.TestUtils;
import ru.veselov.companybot.util.UserActionsUtils;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = PostgresTestContainersConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ContactBotIntegrationTest {

    @MockBean
    CompanyBot bot;

    @MockBean
    BotConfig botConfig;

    @Autowired
    TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    UserStateCache userStateCache;

    @AfterEach
    void clear() {
        customerRepository.deleteAll();
        contactRepository.deleteAll();
        userStateCache.reset();
    }

    @Test
    @SneakyThrows
    void shouldSaveUserGetAllContactsAndSaveIt() {
        pressStartCallContact();
        Update emailPressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.EMAIL);
        BotApiMethod<?> emailAnswer = telegramFacadeUpdateHandler.processUpdate(emailPressed);
        Assertions.assertThat(emailAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update inputEmail = UserActionsUtils.userSendMessageWithContact(TestUtils.USER_EMAIL);
        BotApiMethod<?> inputEmailAnswer = telegramFacadeUpdateHandler.processUpdate(inputEmail);
        Assertions.assertThat(inputEmailAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update namePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.NAME);
        BotApiMethod<?> nameAnswer = telegramFacadeUpdateHandler.processUpdate(namePressed);
        Assertions.assertThat(nameAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update inputName = UserActionsUtils.userSendMessageWithContact(TestUtils.USER_LAST_NAME);
        BotApiMethod<?> inputNameAnswer = telegramFacadeUpdateHandler.processUpdate(inputName);
        Assertions.assertThat(inputNameAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update savePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SAVE);
        BotApiMethod<?> saveAnswer = telegramFacadeUpdateHandler.processUpdate(savePressed);
        Assertions.assertThat(saveAnswer).isInstanceOf(AnswerCallbackQuery.class);

        Mockito.verify(bot, Mockito.times(1)).execute(Mockito.any(SendMessage.class));

        List<CustomerEntity> customers = customerRepository.findAll();
        Assertions.assertThat(customers).hasSize(1);
        CustomerEntity customerEntity = customers.get(0);
        Assertions.assertThat(customerEntity.getId()).isEqualTo(TestUtils.getSimpleUser().getId());
        List<ContactEntity> contacts = contactRepository.findAll();
        Assertions.assertThat(contacts).hasSize(1);
        ContactEntity contactEntity = contacts.get(0);
        Assertions.assertThat(contactEntity.getEmail()).isEqualTo(TestUtils.USER_EMAIL);
        Assertions.assertThat(contactEntity.getLastName()).startsWith(TestUtils.USER_LAST_NAME);
    }

    @Test
    void shouldSaveUserGetSharedContactAndSavedIt() {
        pressStartCallContact();
        Update sharedPressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SHARED);
        BotApiMethod<?> sharedAnswer = telegramFacadeUpdateHandler.processUpdate(sharedPressed);
        Assertions.assertThat(sharedAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update inputShared = UserActionsUtils.userAttachedSharedContact(TestUtils.getUserContact());
        BotApiMethod<?> inputSharedAnswer = telegramFacadeUpdateHandler.processUpdate(inputShared);
        Assertions.assertThat(inputSharedAnswer).isInstanceOf(EditMessageReplyMarkup.class);

        Update savePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SAVE);
        telegramFacadeUpdateHandler.processUpdate(savePressed);

        List<CustomerEntity> customers = customerRepository.findAll();
        Assertions.assertThat(customers).hasSize(1);
        CustomerEntity customerEntity = customers.get(0);
        Assertions.assertThat(customerEntity.getId()).isEqualTo(TestUtils.getSimpleUser().getId());
        List<ContactEntity> contacts = contactRepository.findAll();
        Assertions.assertThat(contacts).hasSize(1);
        ContactEntity contactEntity = contacts.get(0);
        Assertions.assertThat(contactEntity.getPhone()).isEqualTo(TestUtils.USER_PHONE);
        Assertions.assertThat(contactEntity.getLastName()).isEqualTo(TestUtils.USER_LAST_NAME);
        Assertions.assertThat(contactEntity.getFirstName()).isEqualTo(TestUtils.USER_FIRST_NAME);
    }

    @Test
    void shouldSendMessageForWrongInputWrongContact() {

    }

    private void pressStartCallContact() {
        Update startPressed = UserActionsUtils.userPressStart();
        BotApiMethod<?> startAnswer = telegramFacadeUpdateHandler.processUpdate(startPressed);
        Assertions.assertThat(startAnswer).isInstanceOf(SendMessage.class);

        Update callPressed = UserActionsUtils.userPressCall();
        BotApiMethod<?> callAnswer = telegramFacadeUpdateHandler.processUpdate(callPressed);
        Assertions.assertThat(callAnswer).isInstanceOf(SendMessage.class);

        Update contactPressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.CONTACT);
        BotApiMethod<?> contactAnswer = telegramFacadeUpdateHandler.processUpdate(contactPressed);
        Assertions.assertThat(contactAnswer).isInstanceOf(EditMessageReplyMarkup.class);
    }


}
