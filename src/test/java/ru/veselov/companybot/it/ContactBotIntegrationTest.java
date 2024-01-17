package ru.veselov.companybot.it;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.BotState;
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
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUtils;
import ru.veselov.companybot.util.UserActionsUtils;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Slf4j
class ContactBotIntegrationTest extends PostgresTestContainersConfiguration {

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

    @Captor
    ArgumentCaptor<SendMessage> sendMessageCaptor;

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

    @ParameterizedTest
    @MethodSource("getIncorrectContacts")
    @SneakyThrows
    void shouldSendMessageForWrongInputWrongContact(String callbackData, String wrongValue) {
        pressStartCallContact();
        Update contactInputPressed = UserActionsUtils.userPressCallbackButton(callbackData);
        telegramFacadeUpdateHandler.processUpdate(contactInputPressed);

        Update inputValue = UserActionsUtils.userSendMessageWithContact(wrongValue);
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(inputValue);
        Assertions.assertThat(errorAnswer).isNotNull()
                .isInstanceOf(EditMessageReplyMarkup.class);

        Mockito.verify(bot, Mockito.times(1)).execute(sendMessageCaptor.capture());
        SendMessage captured = sendMessageCaptor.getValue();
        Assertions.assertThat(captured.getText())
                .as("Check if message contains info about wrong input")
                .containsAnyOf(
                        MessageUtils.WRONG_EMAIL,
                        MessageUtils.WRONG_PHONE,
                        MessageUtils.NAME_TOO_LONG,
                        MessageUtils.WRONG_CONTACT_FORMAT
                );
    }

    @Test
    @SneakyThrows
    void shouldSendMessageNotEnoughDataForSavingWithMarkedKeyboard() {
        pressStartCallContact();
        Update namePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.NAME);
        telegramFacadeUpdateHandler.processUpdate(namePressed);
        Update inputName = UserActionsUtils.userSendMessageWithContact(TestUtils.USER_LAST_NAME);
        telegramFacadeUpdateHandler.processUpdate(inputName);

        Update savePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SAVE);
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(savePressed);
        Assertions.assertThat(errorAnswer).isNotNull()
                .isInstanceOf(EditMessageReplyMarkup.class);
        EditMessageReplyMarkup editMessage = (EditMessageReplyMarkup) errorAnswer;
        Assertions.assertThat(editMessage.getReplyMarkup()).as("Check if message has reply markup keyboard")
                .isNotNull().isInstanceOf(InlineKeyboardMarkup.class);
        InlineKeyboardMarkup replyMarkup = editMessage.getReplyMarkup();
        InlineKeyboardButton inlineKeyboardButton = replyMarkup.getKeyboard().get(0).get(0);
        Assertions.assertThat(inlineKeyboardButton.getText()).as("Check if returned keyboard with marked button")
                .startsWith(EmojiParser.parseToUnicode(MessageUtils.WHITE_CHECK_MARK));
        Mockito.verify(bot, Mockito.times(1)).execute(sendMessageCaptor.capture());
        SendMessage sendMessage = sendMessageCaptor.getValue();
        Assertions.assertThat(sendMessage.getText()).as("Check if send message contains info about error")
                .isEqualTo(MessageUtils.NOT_ENOUGH_CONTACT);
    }

    @Test
    void shouldReturnSendMessageForWrongBotStateWhenInputContactMessage() {
        pressStartCallContact();
        Update namePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.NAME);
        telegramFacadeUpdateHandler.processUpdate(namePressed);

        userStateCache.setUserBotState(TestUtils.USER_ID, BotState.AWAIT_SHARED);
        Update inputName = UserActionsUtils.userSendMessageWithContact(TestUtils.USER_LAST_NAME);
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(inputName);
        SendMessage sendMessage = (SendMessage) errorAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.WRONG_ACTION_AWAIT_INPUT_TEXT);
    }

    @Test
    void shouldReturnSendMessageForWrongBotStateWhenInputSharedContact() {
        pressStartCallContact();
        Update namePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SHARED);
        telegramFacadeUpdateHandler.processUpdate(namePressed);

        userStateCache.setUserBotState(TestUtils.USER_ID, BotState.AWAIT_EMAIL);
        Update inputShared = UserActionsUtils.userAttachedSharedContact(TestUtils.getUserContact());
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(inputShared);
        SendMessage sendMessage = (SendMessage) errorAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.WRONG_ACTION_AWAIT_SHARED);
    }

    @Test
    void shouldReturnAnswerCallbackQueryWithUnexpectedActionMessage() {
        log.info("User press call command");
        pressStartCallContact();
        Update wrongCallback = UserActionsUtils.userPressCallbackButton("Something wrong from callback");
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(wrongCallback);

        log.info("Check if we receive good answer");
        Assertions.assertThat(errorAnswer).isInstanceOf(AnswerCallbackQuery.class);
        AnswerCallbackQuery callbackQuery = (AnswerCallbackQuery) errorAnswer;
        Assertions.assertThat(callbackQuery.getText()).isEqualTo(MessageUtils.ANOTHER_ACTION);
        Assertions.assertThat(callbackQuery.getCallbackQueryId()).isEqualTo(TestUtils.CALLBACK_ID);
    }

    @Test
    void shouldReturnSendMessageForUnexpectedAction() {
        log.info("User press call command");
        pressStartCallContact();
        Update namePressed = UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.SHARED);
        telegramFacadeUpdateHandler.processUpdate(namePressed);
        log.info("Set wrong bot state for this");
        userStateCache.setUserBotState(TestUtils.USER_ID, BotState.AWAIT_DIVISION_FOR_INQUIRY);

        Update messageWithName = UserActionsUtils.userSendMessageWithContact(TestUtils.USER_LAST_NAME);
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(messageWithName);
        log.info("Check if answer is ok");
        Assertions.assertThat(errorAnswer).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) errorAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.ANOTHER_ACTION);
        Assertions.assertThat(sendMessage.getChatId()).isEqualTo(TestUtils.USER_ID.toString());
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

    private static Stream<Arguments> getIncorrectContacts() {
        return Stream.of(
                Arguments.of(CallBackButtonUtils.EMAIL, TestUtils.faker.animal().name()),
                Arguments.of(CallBackButtonUtils.PHONE, TestUtils.faker.ancient().god()),
                Arguments.of(CallBackButtonUtils.NAME, "p".repeat(251)),
                Arguments.of(CallBackButtonUtils.NAME, ""),
                Arguments.of(CallBackButtonUtils.EMAIL, "            ")
        );
    }

}
