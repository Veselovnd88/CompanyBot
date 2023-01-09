package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.CompanyBot.bot.CompanyBot;

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
        chat.setId(-100L);
        chat.setTitle("TestChannel");
    }
    @Test
    void saveTest(){

    }





}