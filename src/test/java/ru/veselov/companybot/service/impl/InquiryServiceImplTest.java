package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.companybot.dto.CustomerResponseDTO;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.entity.CustomerMessageEntity;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.util.TestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InquiryServiceImplTest {

    @Mock
    InquiryRepository inquiryRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    DivisionRepository divisionRepository;

    @InjectMocks
    InquiryServiceImpl inquiryService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(inquiryService, "inquiryMapper", TestUtils.getInquiryMapper());
    }

    @Test
    void findAll_AllOk_ReturnDTOs() {
        InquiryEntity inquiryEntity = new InquiryEntity();
        inquiryEntity.setInquiryId(UUID.randomUUID());
        DivisionEntity divisionEntity = TestUtils.getDivisionEntity();
        inquiryEntity.setDivision(divisionEntity);
        inquiryEntity.setDate(LocalDateTime.now());
        CustomerMessageEntity message = TestUtils.getCustomerMessageEntity("message");
        inquiryEntity.addMessage(message);
        CustomerEntity customerEntity = TestUtils.getCustomerEntity();
        customerEntity.setContacts(Set.of(TestUtils.getContactEntity()));
        inquiryEntity.setCustomer(customerEntity);
        Mockito.when(inquiryRepository.findAll()).thenReturn(List.of(inquiryEntity));

        List<InquiryResponseDTO> inquiries = inquiryService.findAll();

        Assertions.assertThat(inquiries).hasSize(1).extracting(InquiryResponseDTO::getInquiryId).doesNotContainNull()
                .containsExactly(inquiryEntity.getInquiryId());
        InquiryResponseDTO inquiryResponseDTO = inquiries.get(0);
        System.out.println(inquiryResponseDTO);
        Assertions.assertThat(inquiryResponseDTO.getDivision())
                .extracting(DivisionModel::getDivisionId, DivisionModel::getName, DivisionModel::getDescription)
                .containsExactly(divisionEntity.getDivisionId(), divisionEntity.getName(), divisionEntity.getDescription());
        Assertions.assertThat(inquiryResponseDTO.getCustomer()).extracting(CustomerResponseDTO::getId,
                        CustomerResponseDTO::getFirstName,
                        CustomerResponseDTO::getLastName, CustomerResponseDTO::getUserName)
                .containsExactly(customerEntity.getId(), customerEntity.getFirstName(), customerEntity.getLastName(),
                        customerEntity.getUserName());
    }

}