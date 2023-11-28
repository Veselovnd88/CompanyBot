package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.repository.ContactRepository;
import ru.veselov.companybot.repository.CustomerRepository;
import ru.veselov.companybot.service.CustomerService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public void save(User user) {
        Optional<CustomerEntity> one = findOne(user.getId());
        if (one.isEmpty()) {
            customerRepository.save(toCustomer(user));//FIXME map
            log.info("New user with [id: {}] saved", user.getId());
        } else {
            customerRepository.save(toCustomer(user));
            log.info("New user with [id: {}] updated", user.getId());
        }
    }

    @Override
    public Optional<CustomerEntity> findOne(Long userId) {
        return customerRepository.findById(userId);
    }

    @Override
    public Optional<CustomerEntity> findOneWithContacts(Long userId) {
        return customerRepository.findOneWithContacts(userId);
    }

    @Override
    @Transactional
    public void remove(User user) {
        customerRepository.deleteById(user.getId());
    }

    @Override
    public List<CustomerEntity> findAll() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public void saveContact(ContactModel contact) {
        Optional<CustomerEntity> one = customerRepository.findOneWithContacts(contact.getUserId());
        if (one.isPresent()) {
            ContactEntity contactEntity = toContactEntity(contact);
            contactEntity.setCustomerEntity(one.get());
            contactRepository.save(contactEntity);
            log.info("New contact with [id: {}] saved", contact.getUserId());
        }
    }

}
