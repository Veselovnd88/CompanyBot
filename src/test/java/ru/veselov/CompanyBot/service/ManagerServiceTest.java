package ru.veselov.CompanyBot.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.exception.NoSuchManagerException;
import ru.veselov.CompanyBot.model.DivisionModel;

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
    ManagerModel manager;

    @BeforeEach
    void init(){
        manager = new ManagerModel();
        manager.setManagerId(100L);
        manager.setLastName("Last");
        manager.setFirstName("First");
        manager.setUserName("UserName");
    }
    @Test
    @SneakyThrows
    void saveAndDeleteTest() {
        //Проверка сохранения в бд
        managerService.save(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        managerService.remove(manager);
        assertThrows(NoSuchManagerException.class,
                ()-> managerService.findOne(manager.getManagerId()));
    }

    @Test
    @SneakyThrows
    void saveWithDivisionsFromDB(){
        manager.setDivisions(setUpDivisions());
        managerService.save(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        assertEquals(2,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
    }

    @Test
    @SneakyThrows
    void saveWithDivisionsWithoutDB(){
        manager.setDivisions(setUpDivisions());
        managerService.save(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        assertEquals(2,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
        manager.setDivisions(new HashSet<>());
        managerService.save(manager);
        assertEquals(0,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
    }


    @Test
    @SneakyThrows
    void saveWithDivisionsFromDbAndFromHere(){
        DivisionModel division = new DivisionModel();
        division.setDivisionId("VS");
        division.setName("Vasya");
        Set<DivisionModel> divisionModels = new HashSet<>(setUpDivisions());
        divisionModels.add(division);
        manager.setDivisions(divisionModels);
        managerService.save(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        assertEquals(2,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
    }

    @Test
    @SneakyThrows
    void updateWithDivisionsFromDB(){
        DivisionModel division = new DivisionModel();
        division.setDivisionId("VS");
        division.setName("Vasya");
        manager.setDivisions(setUpDivisions());
        managerService.save(manager);
        manager.setDivisions(Set.of(division));
        managerService.save(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        assertEquals(1,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
    }

    @Test
    @SneakyThrows
    void removeDivisionsTest(){
        manager.setDivisions(setUpDivisions());
        managerService.save(manager);
        managerService.removeDivisions(manager);
        assertInstanceOf(ManagerModel.class, managerService.findOne(100L));
        assertEquals("UserName",managerService.findOne(100L).getUserName());
        assertEquals(0,managerService.findOneWithDivisions(manager.getManagerId()).getDivisions().size());
    }

    @Test
    @SneakyThrows
    void removeManagerTest(){
        Set<DivisionModel> divisionModels = setUpDivisions();
        manager.setDivisions(setUpDivisions());
        managerService.save(manager);
        managerService.remove(manager);
        assertThrows(NoSuchManagerException.class,
                ()-> managerService.findOne(100L));
        for(var d: divisionModels){
            boolean b = divisionService.findOneWithManagers(d).getManagers()
                    .stream().anyMatch(x -> x.getManagerId().equals(manager.getManagerId()));
            assertFalse(b);
        }
    }

    private Set<DivisionModel> setUpDivisions(){
        DivisionModel division = new DivisionModel();
        division.setDivisionId("VS");
        division.setName("Vasya");
        divisionService.save(division);
        DivisionModel division1 = new DivisionModel();
        division1.setDivisionId("PT");
        division1.setName("Petya");
        divisionService.save(division1);
        return Set.of(division,division1);
    }




}