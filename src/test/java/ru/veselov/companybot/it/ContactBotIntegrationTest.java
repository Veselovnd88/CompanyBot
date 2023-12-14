package ru.veselov.companybot.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.config.BotConfig;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainersConfiguration.class)
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

    @AfterEach
    void clear() {
        customerRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    void shouldSaveUserGetAllContactsAndSaveIt() {
        Update startPressed = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.START);
        telegramFacadeUpdateHandler.processUpdate(startPressed);
        Update callPressed = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);
        telegramFacadeUpdateHandler.processUpdate(callPressed);
        Update emailPressed = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(CallBackButtonUtils.EMAIL);
        telegramFacadeUpdateHandler.processUpdate(emailPressed);
        Update inputEmail = TestUpdates
                .getUpdateWithMessageNoCommandNoEntitiesWithContactDataByUser(TestUtils.USER_EMAIL);
        telegramFacadeUpdateHandler.processUpdate(inputEmail);
        Update namePressed = TestUpdates
                .getUpdateWithMessageNoCommandNoEntitiesWithContactDataByUser(CallBackButtonUtils.NAME);
        telegramFacadeUpdateHandler.processUpdate(namePressed);
        Update inputName = TestUpdates
                .getUpdateWithMessageNoCommandNoEntitiesWithContactDataByUser(TestUtils.USER_LAST_NAME);
        telegramFacadeUpdateHandler.processUpdate(inputName);
        Update savePressed = TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(CallBackButtonUtils.SAVE);
        telegramFacadeUpdateHandler.processUpdate(savePressed);


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


}
