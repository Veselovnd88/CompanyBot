package ru.veselov.companybot.it;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.bot.util.BotUtils;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;
import ru.veselov.companybot.util.UserActionsUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class InquiryBotIntegrationTest extends PostgresTestContainersConfiguration {

    @Value("${bot.max-messages}")
    private Integer maxMessages;

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
    InquiryRepository inquiryRepository;

    @Autowired
    UserStateCache userStateCache;

    @Autowired
    DivisionKeyboardHelper divisionKeyboardHelper;

    @AfterEach
    void clear() {
        customerRepository.deleteAll();
        contactRepository.deleteAll();
        inquiryRepository.deleteAll();
        userStateCache.reset();
    }

    @Test
    @Order(1)
    void shouldReturnSendMessageWithKeyboardAfterInquiryChoosing() {
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressStart());
        Update inquiryPressed = UserActionsUtils.userPressInquiryButton();
        BotApiMethod<?> inquiryAnswer = telegramFacadeUpdateHandler.processUpdate(inquiryPressed);
        Assertions.assertThat(inquiryAnswer).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) inquiryAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.CHOOSE_DEP);
        Assertions.assertThat(sendMessage.getReplyMarkup()).isNotNull();
    }

    @Test
    @Order(2)
    void shouldReturnSendMessageAfterChoosingDivisionForInquiry() {
        pressStartAndInquiry();
        Map<String, DivisionModel> cachedDivisions = divisionKeyboardHelper.getCachedDivisions();
        Assertions.assertThat(cachedDivisions).isNotEmpty();
        Update userChooseDiv = UserActionsUtils
                .userPressCallbackButton(cachedDivisions.keySet().stream().toList().get(0));
        BotApiMethod<?> afterDivAnswer = telegramFacadeUpdateHandler.processUpdate(userChooseDiv);
        Assertions.assertThat(afterDivAnswer).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) afterDivAnswer;
        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.INVITATION_TO_INPUT_INQUIRY);
    }

    @Test
    @Order(3)
    @SneakyThrows
    void shouldGetMessagesForInquiryGetContactSaveAllAndSendMessagesByBot() {
        pressStartAndInquiry();
        chooseDivision();
        Update userSendTextMessage = UserActionsUtils.userSendTextMessage();
        telegramFacadeUpdateHandler.processUpdate(userSendTextMessage);
        BotApiMethod<?> saveAnswer = pressContactInputContactAndPressSave();
        Assertions.assertThat(saveAnswer).isInstanceOf(AnswerCallbackQuery.class);

        Mockito.verify(bot, Mockito.times(3)).execute(Mockito.any(SendMessage.class));

        List<CustomerEntity> customers = customerRepository.findAll();
        Assertions.assertThat(customers).hasSize(1);
        CustomerEntity customerEntity = customers.get(0);
        Assertions.assertThat(customerEntity.getId()).isEqualTo(TestUtils.getSimpleUser().getId());
        List<ContactEntity> contacts = contactRepository.findAll();
        Assertions.assertThat(contacts).hasSize(1);
        ContactEntity contactEntity = contacts.get(0);
        Assertions.assertThat(contactEntity.getEmail()).isEqualTo(TestUtils.USER_EMAIL);
        Assertions.assertThat(contactEntity.getLastName()).isEqualTo(TestUtils.USER_LAST_NAME);
    }

    @ParameterizedTest
    @MethodSource("getNotSupportedMessages")
    void shouldReturnSendMessageIfMessageProcessingErrorOccurred(Update update, String message) {
        pressStartAndInquiry();
        chooseDivision();
        BotApiMethod<?> errorAnswer = telegramFacadeUpdateHandler.processUpdate(update);

        Assertions.assertThat(errorAnswer).isInstanceOf(SendMessage.class);
        SendMessage errorAnswerSendMessage = (SendMessage) errorAnswer;
        Assertions.assertThat(errorAnswerSendMessage.getText()).isEqualTo(message);
    }

    @Test
    void shouldReturnSendMessageWithInvitationToInputContactsIfQntOfMessagesMoreThanWeWant() {
        pressStartAndInquiry();
        chooseDivision();
        for (int i = 0; i < maxMessages + 1; i++) {
            Update userSendTextMessage = UserActionsUtils.userSendTextMessage();
            telegramFacadeUpdateHandler.processUpdate(userSendTextMessage);
        }
        //user will receive such message until he doesn't press contact button
        for (int i = 0; i < 1; i++) {
            Update userSendTextMessage = UserActionsUtils.userSendTextMessage();
            BotApiMethod<?> lastAnswer = telegramFacadeUpdateHandler.processUpdate(userSendTextMessage);
            Assertions.assertThat(lastAnswer).isInstanceOf(SendMessage.class);
            SendMessage sendMessageAnswer = (SendMessage) lastAnswer;
            Assertions.assertThat(sendMessageAnswer.getText()).isEqualTo(MessageUtils.MAX_MESSAGES_QNT.formatted(15));
        }
        pressContactInputContactAndPressSave();

        List<InquiryEntity> allInquiries = inquiryRepository.findAll();
        Assertions.assertThat(allInquiries).hasSize(1);
        InquiryEntity inquiryEntity = allInquiries.get(0);
        Optional<InquiryEntity> inquiryWithMsg = inquiryRepository.findByIdWithMessages(inquiryEntity.getInquiryId());
        Assertions.assertThat(inquiryWithMsg).isPresent();
        Assertions.assertThat(inquiryWithMsg.get().getMessages()).hasSize(15);
    }


    private void pressStartAndInquiry() {
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressStart());
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressInquiryButton());
    }

    private void chooseDivision() {
        Map<String, DivisionModel> cachedDivisions = divisionKeyboardHelper.getCachedDivisions();
        Assertions.assertThat(cachedDivisions).isNotEmpty();
        Update userChooseDiv = UserActionsUtils
                .userPressCallbackButton(cachedDivisions.keySet().stream().toList().get(0));
        telegramFacadeUpdateHandler.processUpdate(userChooseDiv);
    }

    private static Stream<Arguments> getNotSupportedMessages() {
        Update messageWithLongCaption = TestUpdates.getUpdateWithMessageWithPhotoByUser();
        messageWithLongCaption.getMessage().setCaption("i".repeat(1025));
        Update messageWithCustomEmoji = TestUpdates.getUpdateWithMessageWithTextContentByUser();
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType(BotUtils.CUSTOM_EMOJI);
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        messageWithCustomEmoji.getMessage().setEntities(List.of(messageEntity));
        return Stream.of(
                Arguments.of(messageWithLongCaption, MessageUtils.CAPTION_TOO_LONG),
                Arguments.of(messageWithCustomEmoji, MessageUtils.NO_CUSTOM_EMOJI)
        );
    }

    private BotApiMethod<?> pressContactInputContactAndPressSave() {
        Update inputContactButton = UserActionsUtils.userPressInputContactButton();
        telegramFacadeUpdateHandler.processUpdate(inputContactButton);
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.EMAIL));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userSendMessageWithContact(TestUtils.USER_EMAIL));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.NAME));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils
                .userSendMessageWithContact(TestUtils.USER_LAST_NAME));
        return telegramFacadeUpdateHandler.processUpdate(UserActionsUtils
                .userPressCallbackButton(CallBackButtonUtils.SAVE));
    }

}
