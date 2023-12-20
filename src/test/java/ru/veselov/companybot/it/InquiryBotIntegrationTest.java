package ru.veselov.companybot.it;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUtils;
import ru.veselov.companybot.util.UserActionsUtils;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
public class InquiryBotIntegrationTest extends PostgresTestContainersConfiguration {

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
        Map<String, DivisionModel> cachedDivisions = divisionKeyboardHelper.getCachedDivisions();
        Assertions.assertThat(cachedDivisions).isNotEmpty();
        Update userChooseDiv = UserActionsUtils
                .userPressCallbackButton(cachedDivisions.keySet().stream().toList().get(0));
        telegramFacadeUpdateHandler.processUpdate(userChooseDiv);
        Update userSendTextMessage = UserActionsUtils.userSendTextMessage();
        telegramFacadeUpdateHandler.processUpdate(userSendTextMessage);
        Update inputContactButton = UserActionsUtils.userPressInputContactButton();
        telegramFacadeUpdateHandler.processUpdate(inputContactButton);
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.EMAIL));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userSendMessageWithContact(TestUtils.USER_EMAIL));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressCallbackButton(CallBackButtonUtils.NAME));
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils
                .userSendMessageWithContact(TestUtils.USER_LAST_NAME));
        BotApiMethod<?> saveAnswer = telegramFacadeUpdateHandler.processUpdate(UserActionsUtils
                .userPressCallbackButton(CallBackButtonUtils.SAVE));
        Assertions.assertThat(saveAnswer).isInstanceOf(AnswerCallbackQuery.class);

        Mockito.verify(bot, Mockito.times(2)).execute(Mockito.any(SendMessage.class));

        List<CustomerEntity> customers = customerRepository.findAll();
        Assertions.assertThat(customers).hasSize(1);
        CustomerEntity customerEntity = customers.get(0);
        Assertions.assertThat(customerEntity.getId()).isEqualTo(TestUtils.getSimpleUser().getId());
        List<ContactEntity> contacts = contactRepository.findAll();
        Assertions.assertThat(contacts).hasSize(1);
        ContactEntity contactEntity = contacts.get(0);
        Assertions.assertThat(contactEntity.getPhone()).isEqualTo(TestUtils.USER_PHONE);
        Assertions.assertThat(contactEntity.getLastName()).isEqualTo(TestUtils.USER_LAST_NAME);
    }

    private void pressStartAndInquiry() {
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressStart());
        telegramFacadeUpdateHandler.processUpdate(UserActionsUtils.userPressInquiryButton());
    }


}
