package ru.veselov.CompanyBot.service.sender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.model.ContactModel;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class ContactSenderTest {

    @MockBean
    CompanyBot companyBot;
    @Autowired
    ContactSender contactSender;
    ContactModel contactModel;

    @BeforeEach
    void init(){
        contactModel=new ContactModel();
    }
    @Test
    void contactMessageTest(){
        String expected = """
                Контактное лицо для связи:\s
                Pipkin Vasya Petrovich\s
                Телефон: +79156666666
                Эл. почта: 123@123.ru""";
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setLastName("Pipkin");
        contactModel.setPhone("+79156666666");
        contactModel.setEmail("123@123.ru");
        contactSender.setUpContactSender(contactModel,true);
        assertEquals(expected,contactSender.getContactMessage(contactModel,100L).getText());
    }


    @Test
    void contactMessageNoPhone(){
        String expected = """
                Контактное лицо для связи:\s
                Vasya Petrovich\s
                Эл. почта: 123@123.ru""";
        contactModel.setLastName(null);
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setPhone(null);
        contactModel.setEmail("123@123.ru");
        contactSender.setUpContactSender(contactModel,false);
        assertEquals(expected,contactSender.getContactMessage(contactModel,100L).getText());
    }

    @Test
    void contactMessageNoLastNameTest(){
        String expected = """
                Контактное лицо для связи:\s
                Vasya Petrovich\s
                Телефон: +79156666666
                Эл. почта: 123@123.ru""";
        contactModel.setLastName(null);
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setPhone("+79156666666");
        contactModel.setEmail("123@123.ru");
        contactSender.setUpContactSender(contactModel,false);
        assertEquals(expected,contactSender.getContactMessage(contactModel,100L).getText());
    }
}