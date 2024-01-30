package ru.veselov.companybot.it.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.companybot.config.BotMocks;
import ru.veselov.companybot.config.EnableTestContainers;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.util.MockMvcUtils;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@Import({BotMocks.class})
@ActiveProfiles("test")
@EnableTestContainers
class InquiryControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DivisionRepository divisionRepository;


    @AfterEach
    void clear() {
        inquiryRepository.deleteAll();
        customerRepository.deleteAll();
        divisionRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void findAll_AllOk_ReturnPageWithInquiries() {
        CustomerEntity customerEntity = TestUtils.getCustomerEntity();
        customerEntity.setContacts(Set.of(TestUtils.getContactEntity()));
        CustomerEntity savedCustomer = customerRepository.saveAndFlush(customerEntity);
        InquiryEntity inquiryEntity = TestUtils.getInquiryEntityWithBaseDivisionCustomerMessage(savedCustomer);
        InquiryEntity savedInquiry = inquiryRepository.saveAndFlush(inquiryEntity);

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.getInquiries());

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


}
