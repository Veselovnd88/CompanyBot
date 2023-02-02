package ru.veselov.CompanyBot.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
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
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.model.ContactModel;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.InquiryModel;
import ru.veselov.CompanyBot.model.ManagerModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    InquiryModel inquiryModel;
    ContactModel contactModel;
    DivisionModel divisionModel;

    @BeforeEach
    @SneakyThrows
    void init(){
        inquiryModel =spy(InquiryModel.class);
        divisionModel = DivisionModel.builder().divisionId("LEUZE").build();
        divisionModel.setManagers(new HashSet<>());
        inquiryModel.setDivision(divisionModel);
        inquiryModel.setUserId(100L);
        inquiryModel.setMessages(List.of(new Message()));
        contactModel =spy(ContactModel.class);
        contactModel.setUserId(100L);
        contactModel.setLastName("test");
        contactModel.setEmail("vasya@petya.ru");
        when(divisionService.findOneWithManagers(inquiryModel.getDivision())).thenReturn(divisionModel);
        when(chatService.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    @SneakyThrows
    void sendInquiryNoChatTest() {
        senderService.send(inquiryModel, contactModel);
        verify(bot).execute(any(SendMessage.class));
        assertEquals(0,senderService.getChatTimers().size());
    }

    @Test
    @SneakyThrows
    void sendInquiryWithChatTest() {
        Chat chat = new Chat();
        chat.setId(-100L);
        chat.setTitle("Channel");
        chat.setType("group");
        when(chatService.findAll()).thenReturn(List.of(chat));
        senderService.send(inquiryModel, contactModel);
        verify(bot,times(2)).execute(any(SendMessage.class));
        assertEquals(1,senderService.getChatTimers().size());
    }

    @Test
    @SneakyThrows
    void sendContactTest() {
        try {
            Thread.sleep(chatInterval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        senderService.send(null, contactModel);
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
        contactModel.setFirstName("Vasya");
        contactModel.setSecondName("Petrovich");
        contactModel.setLastName("Pipkin");
        contactModel.setPhone("+79156666666");
        contactModel.setEmail("123@123.ru");
        assertEquals(expected,senderService.getContactMessage(contactModel,100L).getText());
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
        assertEquals(expected,senderService.getContactMessage(contactModel,100L).getText());
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
        assertEquals(expected,senderService.getContactMessage(contactModel,100L).getText());
    }

    @Test
    @SneakyThrows
    void managerMarkingTest(){
        ManagerModel manager1= new ManagerModel();
        manager1.setFirstName("Vasya");
        manager1.setManagerId(105L);
        ManagerModel manager2 = new ManagerModel();
        manager2.setFirstName("Petya");
        manager2.setLastName("Petrov");
        manager2.setManagerId(106L);
        DivisionModel division = DivisionModel.builder().divisionId("L").name("LLL").build();
        manager1.setDivisions(Set.of(division));
        manager2.setDivisions(Set.of(division));
        division.setManagers(Set.of(manager1,manager2));
        when(divisionService.findOneWithManagers(division)).thenReturn(division);
        Chat chat = new Chat();
        inquiryModel.setDivision(division);
        chat.setId(Long.valueOf(adminId));
        assertNotNull(senderService.markManagerForTest(chat, inquiryModel));

    }


}