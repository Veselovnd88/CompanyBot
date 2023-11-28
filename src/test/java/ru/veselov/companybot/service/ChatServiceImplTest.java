package ru.veselov.companybot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.service.impl.ChatServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceImplTest {
    @MockBean
    CompanyBot companyBot;

    @Autowired
    private ChatServiceImpl chatService;

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