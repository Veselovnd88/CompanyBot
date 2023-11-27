package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.veselov.CompanyBot.bot.CompanyBot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class CompanyInfoServiceTest {

    @MockBean
    CompanyBot bot;
    @Autowired
    CompanyInfoService companyInfoService;

    @Test
    void saveTest() {
        //Checking correct saving of message
        Message message = new Message();
        message.setText("Hello");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("bold");
        messageEntity.setOffset(0);
        messageEntity.setLength(3);
        message.setEntities(List.of(messageEntity));
        companyInfoService.save(message);
        assertEquals(message.getText(),companyInfoService.getLast().getText());
    }

    @Test
    void getLastTest() {
        //Checking returning correct last object
        Message message = new Message();
        message.setText("Hello");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("bold");
        messageEntity.setOffset(0);
        messageEntity.setLength(3);
        message.setEntities(List.of(messageEntity));
        companyInfoService.save(message);
        Message message1 = new Message();
        message1.setText("Hello123");
        MessageEntity messageEntity1 = new MessageEntity();
        messageEntity1.setType("bold");
        messageEntity1.setOffset(0);
        messageEntity1.setLength(3);
        message1.setEntities(List.of(messageEntity1));
        companyInfoService.save(message1);
        assertEquals(message1.getText(),companyInfoService.getLast().getText());
    }
}