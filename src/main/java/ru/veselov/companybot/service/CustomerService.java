package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.entity.CustomerEntity;

import java.util.List;

public interface CustomerService {

    void save(User user);

    void remove(User user);

    List<CustomerEntity> findAll();

}
