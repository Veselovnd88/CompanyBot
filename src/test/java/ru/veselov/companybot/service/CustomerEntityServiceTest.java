package ru.veselov.companybot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class CustomerEntityServiceTest {
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
        ContactModel contact = ContactModel.builder().userId(user.getId()).email("vasya").build();
        customerService.saveContact(contact);
        assertTrue(customerService.findOne(100L).isPresent());
        assertEquals(1,customerService.findOneWithContacts(100L).get().getContacts().size());
        customerService.remove(user);
        assertEquals(0,customerService.findAll().size());
    }
}