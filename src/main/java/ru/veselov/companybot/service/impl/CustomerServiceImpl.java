package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.mapper.CustomerMapper;
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

    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public void save(User user) {
        Optional<CustomerEntity> one = customerRepository.findById(user.getId());
        if (one.isEmpty()) {
            customerRepository.save(customerMapper.toEntity(user));
            log.info("New user with [id: {}] saved", user.getId());
        } else {
            customerRepository.save(customerMapper.toEntity(user));
            log.info("New user with [id: {}] updated", user.getId());
        }
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

}
