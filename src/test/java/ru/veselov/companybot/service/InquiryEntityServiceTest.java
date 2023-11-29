package ru.veselov.companybot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;
import ru.veselov.companybot.service.impl.InquiryServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class InquiryEntityServiceTest {
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private InquiryServiceImpl inquiryService;
    @Autowired
    private DivisionServiceImpl divisionService;
    @Autowired
    private CustomerService customerService;
    User user;
    InquiryModel inquiry;
    Message message;


    @BeforeEach
    void init(){
        user = new User();
        user.setId(100L);
        user.setLastName("Last");
        user.setFirstName("First");
        user.setUserName("UserName");
        inquiry=new InquiryModel();

        inquiry.setDivision(DivisionModel.builder().divisionId(UUID.randomUUID()).build());
        message = new Message();
        message.setText("Test");
        inquiry.setMessages(List.of(message));
        inquiry.setUserId(user.getId());
    }

    @Test
    @DisplayName("Testing how to save inquiries")
    void saveTest() {
        DivisionModel leuze = DivisionModel.builder().divisionId(UUID.randomUUID()).build();
        divisionService.save(leuze);
        customerService.save(user);
        for(int i=0; i<10; i++){
            inquiry.setUserId(user.getId());
            message = new Message();
            message.setText("Test "+i);
            inquiry.setMessages(List.of(message));
            InquiryEntity save = inquiryService.save(inquiry);
            assertTrue(inquiryService.findWithMessages(save.getInquiryId()).isPresent());
            assertEquals(1,inquiryService.findWithMessages(save.getInquiryId()).get().getMessages().size());
        }
        List<InquiryEntity> all = inquiryService.findAll();
        assertEquals(10,all.size());
    }
}