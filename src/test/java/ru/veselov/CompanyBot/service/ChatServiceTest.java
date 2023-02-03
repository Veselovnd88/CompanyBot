package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.service.ChatService;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    private ChatService chatService;

    private Chat chat;
    @BeforeEach
    void init(){
        chat=new Chat();
        chat.setId(-340L);
        chat.setType("channel");
        chat.setTitle("TestChannel");
    }
    @Test
    void saveAndRemoveTest(){
        chatService.save(chat);
        assertEquals(1,chatService.findAll().size());
        assertEquals(-340L,chatService.findAll().get(0).getId());
        chatService.remove(chat.getId());
        assertEquals(0,chatService.findAll().size());
    }





}