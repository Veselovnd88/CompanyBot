package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class ManagerServiceTest {

    @MockBean
    private CompanyBot companyBot;

    @Autowired
    private ManagerService managerService;
    @Autowired
    private DivisionService divisionService;
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
        managerService.save(user);
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        managerService.remove(user);
        assertFalse(managerService.findOne(user.getId()).isPresent());
    }

    @Test
    void saveWithDivisions(){
        Division division = new Division();
        division.setName("Vasya");
        divisionService.save(division);
        Division division1 = new Division();
        division1.setName("Petya");
        divisionService.save(division1);
        managerService.saveWithDivisions(user, new HashSet<>(divisionService.findAll()));
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        System.out.println(managerService.findOneWithDivisions(user.getId()).get().getDivisions());
        assertEquals(2,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());

    }


}