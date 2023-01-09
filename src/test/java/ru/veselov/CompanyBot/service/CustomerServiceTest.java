package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.dao.CustomerDAO;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceTest {
    @MockBean
    private CompanyBot companyBot;

    @Autowired
    private CustomerService customerService;
    User user;

    @BeforeEach
    void init(){
        user = new User();
        user.setId(100L);
        user.setLastName("Last");
        user.setFirstName("First");
        user.setUserName("UserName");
    }
    @Test
    void saveAndDeleteTest() {
        //Проверка сохранения в бд
        customerService.save(user);
        assertTrue(customerService.findOne(100L).isPresent());
        assertEquals("UserName",customerService.findOne(100L).get().getUserName());
        customerService.remove(user);
        assertFalse(customerService.findOne(user.getId()).isPresent());
    }

    @Test
    void update(){
        //Проверка апдейта сущности
        customerService.save(user);
        user.setUserName("Test2");
        customerService.save(user);
        assertTrue(customerService.findOne(100L).isPresent());
        assertEquals("Test2",customerService.findOne(100L).get().getUserName());
    }

    @Test
    void saveContact(){
        customerService.save(user);
        Message message = new Message();
        message.setText("Text");
        customerService.saveContact(user.getId(),message);
        assertTrue(customerService.findOne(100L).isPresent());
        assertEquals("Text",customerService.findOne(100L).get().getContact().getText());
    }
}