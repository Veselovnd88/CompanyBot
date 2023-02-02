package ru.veselov.CompanyBot.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class DivisionDAOTest {

    @MockBean
    CompanyBot bot;

    @Autowired
    DivisionDAO divisionDAO;

    @Autowired
    DivisionService divisionService;

    @Autowired
    ManagerService managerService;

    @Test
    void findWithManagersTest() {
        DivisionModel divisionModel = DivisionModel.builder().divisionId("LL").name("LLLLLL").build();
        divisionService.save(divisionModel);

        ManagerModel managerModel = new ManagerModel();
        managerModel.setManagerId(100L);
        managerModel.setLastName("Last Name");
        managerModel.setDivisions(Set.of(divisionModel));

        ManagerModel managerModel1 = new ManagerModel();
        managerModel1.setManagerId(101L);
        managerModel1.setLastName("Last Name222");
        managerModel1.setDivisions(Set.of(divisionModel));

        managerService.save(managerModel);
        managerService.save(managerModel1);

        Optional<Division> ll = divisionDAO.findOneWithManagers("LL");
        assertEquals(2,ll.get().getManagers().size());

    }
}