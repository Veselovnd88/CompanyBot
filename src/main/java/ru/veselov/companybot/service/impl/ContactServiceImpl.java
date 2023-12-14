package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.exception.CustomerNotFoundException;
import ru.veselov.companybot.mapper.ContactMapper;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.service.ContactService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final CustomerRepository customerRepository;

    private final ContactMapper contactMapper;

    @Override
    @Transactional
    public void saveContact(ContactModel contact) {
        Long customerId = contact.getUserId();
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(
                        () -> {
                            log.error("Customer with [id: {}] not found", customerId);
                            return new CustomerNotFoundException("Customer with [id: %s] not found"
                                    .formatted(customerId));
                        }
                );
        ContactEntity contactEntity = contactMapper.toEntity(contact);
        contactEntity.setCustomer(customerEntity);
        contactRepository.save(contactEntity);
        log.info("New contact with [id: {}] saved", customerId);
    }

}
