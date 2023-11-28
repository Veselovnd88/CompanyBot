package ru.veselov.companybot.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class DivisionEntityRepositoryTest {

    @MockBean
    CompanyBot bot;

    @Autowired
    DivisionRepository divisionRepository;

    @Autowired
    DivisionServiceImpl divisionService;

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

        Optional<DivisionEntity> ll = divisionRepository.findOneWithManagers("LL");
        assertEquals(2,ll.get().getManagers().size());

    }
}