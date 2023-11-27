package ru.veselov.CompanyBot.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.model.DivisionModel;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@ActiveProfiles("test")
class DivisionServiceTest {
    @MockBean
    CompanyBot bot;

    @Autowired
    DivisionService divisionService;

    @Autowired
    ManagerService managerService;

    DivisionModel division1;
    DivisionModel division2;

    @BeforeEach
    void init(){
        division1 = DivisionModel.builder().divisionId("TL")
                .name("LOTO").build();
        division2 = DivisionModel.builder().divisionId("PL")
                .name("PROLO").build();

    }

    @Test
    @SneakyThrows
    void saveTest(){
        //Checking if division saved correctly
        divisionService.save(division1);
        assertInstanceOf(DivisionModel.class,divisionService.findOne(division1));
        divisionService.save(division2);
        assertInstanceOf(DivisionModel.class,divisionService.findOne(division2));
    }
    @Test
    @SneakyThrows
    void updateTest(){
        //Checking if division updated correctly
        divisionService.save(division1);
        division1.setName("VASYAPETYA");
        divisionService.save(division1);
        assertEquals(division1.getDivisionId(),divisionService.findOne(division1).getDivisionId());
    }

    @Test
    @SneakyThrows
    void removeWithManager(){
        //Checking if divisions removed correctly from the manager
        divisionService.save(division1);
        ManagerModel manager = new ManagerModel();
        manager.setManagerId(100L);
        manager.setUserName("Vasya");
        manager.setDivisions(Set.of(division1));
        managerService.save(manager);
        ManagerModel oneWithDivisions = managerService.findOneWithDivisions(manager.getManagerId());
        assertEquals(1,oneWithDivisions.getDivisions().size());
        divisionService.remove(division1);
        assertEquals(0,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());




    }






}