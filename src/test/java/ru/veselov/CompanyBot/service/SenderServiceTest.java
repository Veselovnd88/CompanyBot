package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.model.Department;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class SenderServiceTest {

    @MockBean
    CompanyBot bot;

    @MockBean
    ChatService chatService;
    @Value("${bot.adminId}")
    private String adminId;
    @Value("${bot.chat-interval}")
    private long chatInterval;

    @Autowired
    SenderService senderService;

    CustomerInquiry customerInquiry;
    CustomerContact customerContact;

    @BeforeEach
    void init(){
        customerInquiry=spy(CustomerInquiry.class);
        customerInquiry.setDepartment(Department.COMMON);
        customerInquiry.setUserId(100L);
        customerInquiry.setMessages(List.of(new Message()));
        customerContact =spy(CustomerContact.class);
        customerContact.setUserId(100L);
        customerContact.setLastName("test");
        customerContact.setEmail("vasya@petya.ru");
    }

    @Test
    void sendInquiryTest() throws TelegramApiException {
        senderService.send(customerInquiry,customerContact);
        verify(bot).execute(SendMessage.builder().chatId(Long.valueOf(adminId))
                .text("Направлен следующий запрос по тематике "+customerInquiry.getDepartment()).build());
        assertEquals(1,senderService.getChatTimers().size());
    }

    @Test
    void sendContactTest() throws TelegramApiException {
        senderService.send(null,customerContact);
        verify(bot).execute(any(SendMessage.class));
        verify(bot,never()).execute(any(SendContact.class));
    }

    @Test
    void contactMessageTest(){
        String expected = """
                Контактное лицо для связи:\s
                Pipkin Vasya Petrovich\s
                Телефон: +79156666666
                Эл. почта: 123@123.ru""";
        customerContact.setFirstName("Vasya");
        customerContact.setSecondName("Petrovich");
        customerContact.setLastName("Pipkin");
        customerContact.setPhone("+79156666666");
        customerContact.setEmail("123@123.ru");
        assertEquals(expected,senderService.getContactMessage(customerContact,100L).getText());
    }
    @Test
    void contactMessageNoLastNameTest(){
        String expected = """
                Контактное лицо для связи:\s
                Vasya Petrovich\s
                Телефон: +79156666666
                Эл. почта: 123@123.ru""";
        customerContact.setLastName(null);
        customerContact.setFirstName("Vasya");
        customerContact.setSecondName("Petrovich");
        customerContact.setPhone("+79156666666");
        customerContact.setEmail("123@123.ru");
        assertEquals(expected,senderService.getContactMessage(customerContact,100L).getText());
    }

    @Test
    void contactMessageNoPhone(){
        String expected = """
                Контактное лицо для связи:\s
                Vasya Petrovich\s
                Эл. почта: 123@123.ru""";
        customerContact.setLastName(null);
        customerContact.setFirstName("Vasya");
        customerContact.setSecondName("Petrovich");
        customerContact.setPhone(null);
        customerContact.setEmail("123@123.ru");
        assertEquals(expected,senderService.getContactMessage(customerContact,100L).getText());
    }

}