package ru.veselov.companybot.bot.util.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.util.TestUtils;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ContactMessageProcessorImplTest {

    public Faker faker = new Faker();

    private final String LAST_NAME = faker.name().lastName();

    private final String FIRST_NAME = faker.name().firstName();

    private final String SECOND_NAME = faker.name().firstName();

    @Mock
    KeyBoardUtils keyBoardUtils;

    @Mock
    EmailValidator emailValidator;

    @InjectMocks
    ContactMessageProcessorImpl contactMessageProcessor;

    @Test
    void shouldProcessFullName() {
        String name = LAST_NAME + " " + FIRST_NAME + " " + SECOND_NAME;
        ContactModel contact = new ContactModel();
        contactMessageProcessor.processName(contact, name);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isEqualTo(SECOND_NAME),
                () -> Mockito.verify(keyBoardUtils).editMessageSavedField(Mockito.any(), Mockito.any())
        );

    }

    @Test
    void shouldProcessOnlyLastName() {
        ContactModel contact = new ContactModel();
        contactMessageProcessor.processName(contact, LAST_NAME);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isNull(),
                () -> Assertions.assertThat(contact.getSecondName()).isNull(),
                () -> Mockito.verify(keyBoardUtils).editMessageSavedField(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldProcessOnlyFirstAndLastName() {
        String name = LAST_NAME + " " + FIRST_NAME;
        ContactModel contact = new ContactModel();
        contactMessageProcessor.processName(contact, name);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isNull(),
                () -> Mockito.verify(keyBoardUtils).editMessageSavedField(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldProcessMoreThanThreePartsOfName() {
        String name = LAST_NAME + " " + FIRST_NAME + " " + SECOND_NAME + " " + FIRST_NAME;
        ContactModel contact = new ContactModel();
        contactMessageProcessor.processName(contact, name);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isEqualTo(SECOND_NAME + " " + FIRST_NAME),
                () -> Mockito.verify(keyBoardUtils).editMessageSavedField(Mockito.any(), Mockito.any())
        );
    }

    @ParameterizedTest
    @MethodSource("getIncorrectNames")
    void shouldThrowExceptionForIncorrectName(String name) {
        ContactModel contact = ContactModel.builder().userId(TestUtils.USER_ID).build();

        Assertions.assertThatThrownBy(() -> contactMessageProcessor.processName(contact, name))
                .isInstanceOf(ContactProcessingException.class);
        Mockito.verifyNoInteractions(keyBoardUtils);
        Mockito.verifyNoInteractions(emailValidator);
    }

    @Test
    void processPhone() {
    }

    @Test
    void processEmail() {
    }

    private static Stream<String> getIncorrectNames() {
        return Stream.of(
                "",
                " ",
                "a".repeat(251)
        );
    }
}