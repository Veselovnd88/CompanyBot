package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;

import java.util.HashSet;
import java.util.Set;

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
    @MockBean
    CommandLineRunner commandLineRunner;
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
    void saveWithDivisionsFromDB(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        divisionService.save(division);
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        managerService.saveWithDivisions(user, new HashSet<>(divisionService.findAll()));
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        assertEquals(2,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
    }

    @Test
    void saveWithDivisionsWithoutDB(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        managerService.saveWithDivisions(user,Set.of(division,division1));
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        assertEquals(2,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
        managerService.saveWithDivisions(user,new HashSet<>());
        assertEquals(0,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
    }


    @Test
    void saveWithDivisionsMixed(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        Set<Division> dbSet = new HashSet<>(divisionService.findAll());
        dbSet.add(division);
        managerService.saveWithDivisions(user,dbSet);
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        assertEquals(2,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
    }

    @Test
    void updateWithDivisionsFromDB(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        divisionService.save(division);
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        managerService.saveWithDivisions(user, new HashSet<>(divisionService.findAll()));
        managerService.saveWithDivisions(user,Set.of(division1));
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        assertEquals(1,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
    }

    @Test
    void removeDivisionsTest(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        divisionService.save(division);
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        managerService.saveWithDivisions(user, new HashSet<>(divisionService.findAll()));
        managerService.removeDivisions(user);
        assertTrue(managerService.findOne(100L).isPresent());
        assertEquals("UserName",managerService.findOne(100L).get().getUserName());
        assertEquals(0,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());
    }

    @Test
    void removeManagerTest(){
        Division division = new Division();
        division.setDivisionId("VS");
        division.setName("Vasya");
        divisionService.save(division);
        Division division1 = new Division();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        managerService.saveWithDivisions(user, new HashSet<>(divisionService.findAll()));
        managerService.remove(user);
        assertFalse(managerService.findOne(100L).isPresent());
        boolean hasManager = divisionService.findOneWithManagers(division).get()
                .getManagers().stream().anyMatch(x -> x.getManagerId().equals(user.getId()));
        assertFalse(hasManager);
    }




}