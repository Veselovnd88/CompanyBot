package ru.veselov.companybot.bot.util.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Contact;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
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

    ContactModel contact = ContactModel.builder().userId(TestUtils.USER_ID).build();

    @Mock
    ContactKeyboardHelperImpl contactKeyboardHelper;

    @Mock
    EmailValidator emailValidator;

    @InjectMocks
    ContactMessageProcessorImpl contactMessageProcessor;

    @Test
    void shouldProcessFullName() {
        String name = LAST_NAME + " " + FIRST_NAME + " " + SECOND_NAME;

        contactMessageProcessor.processName(contact, name);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isEqualTo(SECOND_NAME),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldProcessOnlyLastName() {
        contactMessageProcessor.processName(contact, LAST_NAME);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isNull(),
                () -> Assertions.assertThat(contact.getSecondName()).isNull(),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldProcessOnlyFirstAndLastName() {
        String name = LAST_NAME + " " + FIRST_NAME;

        contactMessageProcessor.processName(contact, name);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isNull(),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldProcessMoreThanThreePartsOfName() {
        String name = LAST_NAME + " " + FIRST_NAME + " " + SECOND_NAME + " " + FIRST_NAME;

        contactMessageProcessor.processName(contact, name);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getSecondName()).isEqualTo(SECOND_NAME + " " + FIRST_NAME),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @ParameterizedTest
    @MethodSource("getIncorrectNames")
    void shouldThrowExceptionForIncorrectName(String name) {
        Assertions.assertThatThrownBy(() -> contactMessageProcessor.processName(contact, name))
                .isInstanceOf(ContactProcessingException.class);

        Mockito.verifyNoInteractions(contactKeyboardHelper);
        Mockito.verifyNoInteractions(emailValidator);
    }

    @ParameterizedTest
    @ValueSource(strings = {"+79175550335", "89167861234", "8-495-250-23-93", "+2 234 345-24-66"})
    void shouldProcessPhone(String phone) {
        contactMessageProcessor.processPhone(contact, phone);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getPhone()).isEqualTo(phone),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"+7a9175550335", "891", "8-495asdf-250-23-93", "+99999992 234 345-24-66"})
    void shouldThrowExceptionForWrongPhoneFormat(String phone) {
        Assertions.assertThatThrownBy(() -> contactMessageProcessor.processPhone(contact, phone))
                .isInstanceOf(ContactProcessingException.class);
        Mockito.verifyNoInteractions(contactKeyboardHelper);
        Mockito.verifyNoInteractions(emailValidator);
    }

    @Test
    void shouldProcessEmail() {
        Mockito.when(emailValidator.isValid(Mockito.any(), Mockito.any())).thenReturn(true);

        contactMessageProcessor.processEmail(contact, "123@123.com");

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getEmail()).isEqualTo("123@123.com"),
                () -> Mockito.verify(contactKeyboardHelper).getEditMessageReplyAfterSendingContactData(Mockito.any(), Mockito.any())
        );
    }

    @Test
    void shouldThrowExceptionIfEmailIsNotCorrect() {
        Mockito.when(emailValidator.isValid(Mockito.any(), Mockito.any())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> contactMessageProcessor.processEmail(contact, "123"))
                .isInstanceOf(ContactProcessingException.class);

        Mockito.verifyNoInteractions(contactKeyboardHelper);
        Mockito.verify(emailValidator).isValid(Mockito.any(), Mockito.any());
    }

    @Test
    void shouldProcessSharedContact() {
        Contact shared = new Contact();
        shared.setFirstName(FIRST_NAME);
        shared.setLastName(LAST_NAME);
        shared.setPhoneNumber("+1 123 456 78 90");

        contactMessageProcessor.processSharedContact(contact, shared);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(contact.getContact()).isEqualTo(shared),
                () -> Assertions.assertThat(contact.getFirstName()).isEqualTo(FIRST_NAME),
                () -> Assertions.assertThat(contact.getLastName()).isEqualTo(LAST_NAME),
                () -> Assertions.assertThat(contact.getPhone()).isEqualTo(shared.getPhoneNumber())
        );
    }

    private static Stream<String> getIncorrectNames() {
        return Stream.of(
                "",
                " ",
                "a".repeat(251)
        );
    }

}
