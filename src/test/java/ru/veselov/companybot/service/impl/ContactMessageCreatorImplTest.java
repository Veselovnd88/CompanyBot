package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.service.sender.impl.ContactMessageCreatorImpl;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;

class ContactMessageCreatorImplTest {

    @Test
    void shouldReturnListWithOneContactMessage() {
        ContactModel userContactModel = TestUtils.getUserContactModel();
        ContactMessageCreatorImpl contactMessageCreator = new ContactMessageCreatorImpl();

        List<BotApiMethod<?>> messagesToSend = contactMessageCreator
                .createBotMessagesToSend(userContactModel, "100", true);
        Assertions.assertThat(messagesToSend).hasSize(1);
    }

    @Test
    void shouldReturnListWithOneContactMessageAndSharedContact() {
        ContactModel userContactModel = TestUtils.getUserContactModel();
        userContactModel.setContact(TestUtils.getUserContact());
        ContactMessageCreatorImpl contactMessageCreator = new ContactMessageCreatorImpl();

        List<BotApiMethod<?>> messagesToSend = contactMessageCreator
                .createBotMessagesToSend(userContactModel, "100", true);
        Assertions.assertThat(messagesToSend).hasSize(2);
    }

}
