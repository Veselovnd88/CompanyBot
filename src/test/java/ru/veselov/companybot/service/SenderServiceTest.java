package ru.veselov.companybot.service;

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
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.impl.ChatServiceImpl;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;
import ru.veselov.companybot.service.impl.SenderService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    DivisionServiceImpl divisionService;
    InquiryModel inquiryModel;
    ContactModel contactModel;
    DivisionModel divisionModel;

    @BeforeEach
    @SneakyThrows
    void init(){
        inquiryModel =spy(InquiryModel.class);
        divisionModel = DivisionModel.builder().divisionId(UUID.randomUUID()).build();
        inquiryModel.setDivision(divisionModel);
        inquiryModel.setUserId(100L);
        inquiryModel.setMessages(List.of(new Message()));
        contactModel =spy(ContactModel.class);
        contactModel.setUserId(100L);
        contactModel.setLastName("test");
        contactModel.setEmail("vasya@petya.ru");
        when(chatServiceImpl.findAll()).thenReturn(Collections.emptyList());
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