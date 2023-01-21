package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.model.CustomerInquiry;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    @MockBean
    DivisionService divisionService;
    CustomerInquiry customerInquiry;
    CustomerContact customerContact;

    @BeforeEach
    void init(){
        customerInquiry=spy(CustomerInquiry.class);
        customerInquiry.setDivision(Division.builder().divisionId("LEUZE").build());
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
        verify(bot).execute(any(SendMessage.class));
        assertEquals(1,senderService.getChatTimers().size());
    }

    @Test
    void sendContactTest() throws TelegramApiException {
        try {
            Thread.sleep(chatInterval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    @Test
    void managerMarkingTest(){
        ManagerEntity manager1= new ManagerEntity();
        manager1.setFirstName("Vasya");
        manager1.setManagerId(105L);
        ManagerEntity manager2 = new ManagerEntity();
        manager2.setFirstName("Petya");
        manager2.setLastName("Petrov");
        manager2.setManagerId(106L);
        Division division = Division.builder().divisionId("L").name("LLL").build();
        manager1.addDivision(division);
        manager2.addDivision(division);
        when(divisionService.findOneWithManagers(division)).thenReturn(Optional.of(division));
        Chat chat = new Chat();
        customerInquiry.setDivision(division);
        chat.setId(Long.valueOf(adminId));
        assertNotNull(senderService.markManagerForTest(chat,customerInquiry));

    }


}