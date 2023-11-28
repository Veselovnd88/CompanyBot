package ru.veselov.companybot.service.sender;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class InquirySenderTest {
    @Value("${bot.adminId}")
    private String adminId;
    @MockBean
    CompanyBot companyBot;
    @MockBean
    DivisionServiceImpl divisionService;
    @Autowired
    InquirySender inquirySender;
    InquiryModel inquiryModel;

    @Test
    @SneakyThrows
    void managerMarkingTest(){
        inquiryModel = new InquiryModel();
        ManagerModel manager1= new ManagerModel();
        manager1.setFirstName("Vasya");
        manager1.setManagerId(105L);
        ManagerModel manager2 = new ManagerModel();
        manager2.setFirstName("Petya");
        manager2.setLastName("Petrov");
        manager2.setManagerId(106L);
        DivisionModel division = DivisionModel.builder().divisionId("L").name("LLL").build();
        manager1.setDivisions(Set.of(division));
        manager2.setDivisions(Set.of(division));
        division.setManagers(Set.of(manager1,manager2));
        when(divisionService.findOneWithManagers(division)).thenReturn(division);
        Chat chat = new Chat();
        inquiryModel.setDivision(division);
        chat.setId(Long.valueOf(adminId));
        inquirySender.setInquiry(inquiryModel);
        assertNotNull(inquirySender.markManagerForTest(chat, inquiryModel));
    }

}