package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.repository.ContactRepository;
import ru.veselov.CompanyBot.repository.CustomerRepository;
import ru.veselov.CompanyBot.entity.ContactEntity;
import ru.veselov.CompanyBot.entity.CustomerEntity;
import ru.veselov.CompanyBot.model.ContactModel;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public CustomerService(CustomerRepository customerRepository, ContactRepository contactRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.modelMapper = modelMapper;
    }

    public void save(User user){
        Optional<CustomerEntity> one = findOne(user.getId());
        if(one.isEmpty()){
            customerRepository.save(toCustomer(user));
            log.info("{}: новый пользователь  сохранен в БД", user.getId());}
        else{
            customerRepository.update(toCustomer(user));
            log.info("{}: данные пользователя  обновлены в БД", user.getId());
        }
    }


    public Optional<CustomerEntity> findOne(Long userId){
        return customerRepository.findOne(userId);
    }
    public Optional<CustomerEntity> findOneWithContacts(Long userId){
        return customerRepository.findOneWithContacts(userId);
    }
    public void remove(User user){
        customerRepository.deleteById(user.getId());
    }
    public List<CustomerEntity> findAll(){
        return customerRepository.findAll();
    }
    //@Transactional
    public void saveContact(ContactModel contact){
        Optional<CustomerEntity> one = customerRepository.findOneWithContacts(contact.getUserId());
        if(one.isPresent()){
            ContactEntity contactEntity = toContactEntity(contact);
            contactEntity.setCustomerEntity(one.get());
            contactRepository.save(contactEntity);
        }
        log.info("{}: новый контакт  сохранен в БД", contact.getUserId());
    }
    private CustomerEntity toCustomer(User user){
        return modelMapper.map(user, CustomerEntity.class);
    }

    private ContactEntity toContactEntity(ContactModel contact){
        return modelMapper.map(contact,ContactEntity.class);
    }
}
