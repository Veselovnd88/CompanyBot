package ru.veselov.CompanyBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.entity.Inquiry;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.model.Department;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.service.InquiryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class InquiryServiceTest {
    @MockBean
    CompanyBot companyBot;
    @Autowired
    private InquiryService inquiryService;
    @Autowired
    private CustomerService customerService;
    User user;
    CustomerInquiry inquiry;
    Message message;
    @BeforeEach
    void init(){
        user = new User();
        user.setId(100L);
        user.setLastName("Last");
        user.setFirstName("First");
        user.setUserName("UserName");
        inquiry=new CustomerInquiry();
        inquiry.setDepartment(Department.COMMON);
        message = new Message();
        message.setText("Test");
        inquiry.setMessages(List.of(message));
        inquiry.setUserId(user.getId());
    }

    @Test
    @DisplayName("Testing how to save inquiries")
    void saveTest() {
        customerService.save(user);
        for(int i=0; i<10; i++){
            CustomerInquiry inq = new CustomerInquiry();
            inq.setDepartment(Department.COMMON);
            inq.setUserId(user.getId());
            message = new Message();
            message.setText("Test "+i);
            inquiry.setMessages(List.of(message));
            Inquiry save = inquiryService.save(inquiry);
            assertTrue(inquiryService.findWithMessages(save.getInquiryId()).isPresent());
            assertEquals(1,inquiryService.findWithMessages(save.getInquiryId()).get().getMessages().size());
        }
        List<Inquiry> all = inquiryService.findAll();
        assertEquals(10,all.size());
    }
}