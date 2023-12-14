package ru.veselov.companybot.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.veselov.companybot.model.ContactModel;

class ContactMessageCreateTest {

    @Test
    void shouldReturnGoodFormattedStringWithAllContactData() {
        ContactModel contactModel = new ContactModel();
        String expected = """
                Контактное лицо для связи:
                Pipkin Vasya Petrovich\s
                Телефон: +79156666666
                Эл. почта: 123@123.ru""";
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setLastName("Pipkin");
        contactModel.setPhone("+79156666666");
        contactModel.setEmail("123@123.ru");
        String contactMessage = MessageUtils.createContactMessage(contactModel, true);
        Assertions.assertThat(contactMessage).isEqualTo(expected);
    }

    @Test
    void shouldReturnGoodFormattedStringWithoutPhoneAndLastName() {
        ContactModel contactModel = new ContactModel();
        String expected = """
                Контактное лицо для связи:
                Vasya Petrovich\s
                Эл. почта: 123@123.ru""";
        contactModel.setLastName(null);
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setPhone(null);
        contactModel.setEmail("123@123.ru");
        String contactMessage = MessageUtils.createContactMessage(contactModel, true);
        Assertions.assertThat(contactMessage).isEqualTo(expected);
    }

}
