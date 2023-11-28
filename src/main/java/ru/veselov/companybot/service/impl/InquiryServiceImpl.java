package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.entity.CustomerMessageEntity;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.entity.Inquiry;
import ru.veselov.companybot.exception.CustomerNotFoundException;
import ru.veselov.companybot.exception.DivisionNotFoundException;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.repository.InquiryRepository;
import ru.veselov.companybot.service.InquiryService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    private final CustomerRepository customerRepository;

    private final DivisionRepository divisionRepository;

    @Override
    @Transactional
    public Inquiry save(InquiryModel inquiry) {
        Long userId = inquiry.getUserId();
        CustomerEntity customerEntity = customerRepository.findById(userId)
                .orElseThrow(
                        () -> {
                            log.error("Customer with id: {} not found", userId);
                            return new CustomerNotFoundException("Customer with id: %s not found".formatted(userId));
                        }
                );
        Long divisionId = inquiry.getDivision().getDivisionId();
        DivisionEntity divisionEntity = divisionRepository.findById(divisionId).orElseThrow(
                () -> {
                    log.error("Division with id: {} not found", divisionId);
                    return new DivisionNotFoundException("Division with id: %s not found".formatted(divisionId));
                }
        );
        Inquiry inquiryEntity = toInquiryEntity(inquiry);
        inquiryEntity.setCustomerEntity(customerEntity);
        inquiryEntity.setDivisionEntity(divisionEntity);
        log.info("Inquiry of [user: {}] saved", userId);
        return inquiryRepository.save(inquiryEntity);
    }

    @Override
    public Optional<Inquiry> findWithMessages(Long id) {
        return inquiryRepository.findByIdWithMessages(id);
    }

    @Override
    public List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }

    private Inquiry toInquiryEntity(InquiryModel inquiryModel) {
        Inquiry inquiry = new Inquiry();
        inquiry.setDate(new Date());
        for (Message message : inquiryModel.getMessages()) {
            CustomerMessageEntity cme = new CustomerMessageEntity();
            cme.setMessage(message);
            inquiry.addMessage(cme);
        }
        return inquiry;
    }
}
