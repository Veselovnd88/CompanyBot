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
import ru.veselov.CompanyBot.entity.ManagerEntity;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@ActiveProfiles("test")
class DivisionServiceTest {
    @MockBean
    CompanyBot bot;

    @Autowired
    DivisionService divisionService;

    @Autowired
    ManagerService managerService;

    Division division1;
    Division division2;

    @BeforeEach
    void init(){
        division1 = Division.builder().divisionId("TL")
                .name("LOTO").build();
        division2 = Division.builder().divisionId("PL")
                .name("PROLO").build();

    }

    @Test
    void saveTest(){
        //Checking if division saved correctly
        divisionService.save(division1);
        assertTrue(divisionService.findOne(division1).isPresent());
        divisionService.save(division2);
        assertTrue(divisionService.findOne(division2).isPresent());
    }
    @Test
    void updateTest(){
        //Checking if division updated correctly
        divisionService.save(division1);
        division1.setName("VASYAPETYA");
        divisionService.save(division1);
        assertEquals(division1.getDivisionId(),divisionService.findOne(division1).get().getDivisionId());
    }

    @Test
    void removeWithManager(){
        //Checking if divisions removed correctly from the manager
        divisionService.save(division1);
        User user = new User();
        user.setId(100L);
        user.setUserName("Vasya");
        managerService.saveWithDivisions(user, Set.of(division1));
        Optional<ManagerEntity> oneWithDivisions = managerService.findOneWithDivisions(user.getId());
        assertEquals(1,oneWithDivisions.get().getDivisions().size());
        divisionService.remove(division1);
        assertEquals(0,managerService.findOneWithDivisions(user.getId()).get().getDivisions().size());




    }






}