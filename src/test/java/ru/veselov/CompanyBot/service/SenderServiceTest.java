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
import ru.veselov.CompanyBot.service.impl.ChatServiceImpl;
import ru.veselov.CompanyBot.service.impl.SenderService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
    ChatServiceImpl chatServiceImpl;
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
        when(chatServiceImpl.findAll()).thenReturn(Collections.emptyList());
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
        when(chatServiceImpl.findAll()).thenReturn(List.of(chat));
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

}