package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.entity.CustomerEntity;
import ru.veselov.companybot.model.ContactModel;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    void save(User user);

    Optional<CustomerEntity> findOne(Long userId);

    Optional<CustomerEntity> findOneWithContacts(Long userId);

    void remove(User user);

    List<CustomerEntity> findAll();

    void saveContact(ContactModel contact);

}
