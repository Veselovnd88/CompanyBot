package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.bot.util.BotUtils;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.entity.CustomerMessageEntity;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.mapper.InquiryMapper;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.service.InquiryService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    private final CustomerRepository customerRepository;

    private final DivisionRepository divisionRepository;

    private final InquiryMapper inquiryMapper;

    @Override
    @Transactional
    public InquiryEntity save(InquiryModel inquiry) {
        Long userId = inquiry.getUserId();
        CustomerEntity customerEntity = customerRepository.findById(userId).orElseGet(
                () -> {
                    log.warn("Customer with {} was not in db, i will create new entity", userId);
                    CustomerEntity newCustomerEntity = new CustomerEntity();
                    newCustomerEntity.setId(userId);
                    newCustomerEntity.setLastName(userId.toString());
                    return newCustomerEntity;
                });
        UUID divisionId = inquiry.getDivision().getDivisionId();
        Optional<DivisionEntity> divisionOptional = divisionRepository.findById(divisionId);
        DivisionEntity divisionEntity;
        divisionEntity = divisionOptional.orElseGet(() -> {
                    log.warn("Division with id not found, choose base or will created new");
                    return divisionRepository.findByName(BotUtils.BASE_DIVISION)
                            .orElseGet(() -> {
                                log.warn("Base division is not found, created new");
                                return DivisionEntity.builder()
                                        .name(BotUtils.BASE_DIVISION).description("Общ. вопросы")
                                        .build();
                            });
                }
        );
        InquiryEntity inquiryEntity = toInquiryEntity(inquiry);
        inquiryEntity.setCustomer(customerEntity);
        inquiryEntity.setDivision(divisionEntity);
        log.info("Inquiry of [user: {}] saved", userId);
        return inquiryRepository.save(inquiryEntity);
    }

    @Override
    public Optional<InquiryEntity> findWithMessages(UUID id) {
        return inquiryRepository.findByIdWithMessages(id);
    }

    @Override
    public List<InquiryResponseDTO> findAll() {
        List<InquiryResponseDTO> inquiryResponseDTOS = inquiryMapper.entitiesToDTOS(inquiryRepository.findAll());
        log.debug("Retrieved inquiries from DB");
        return inquiryResponseDTOS;
    }

    private InquiryEntity toInquiryEntity(InquiryModel inquiryModel) {
        InquiryEntity inquiryEntity = new InquiryEntity();
        for (Message message : inquiryModel.getMessages()) {
            CustomerMessageEntity cme = new CustomerMessageEntity();
            cme.setMessage(message);
            inquiryEntity.addMessage(cme);
        }
        return inquiryEntity;
    }

}
