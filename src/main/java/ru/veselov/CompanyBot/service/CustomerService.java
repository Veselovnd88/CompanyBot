package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.dao.ContactDAO;
import ru.veselov.CompanyBot.dao.CustomerDAO;
import ru.veselov.CompanyBot.entity.ContactEntity;
import ru.veselov.CompanyBot.entity.Customer;
import ru.veselov.CompanyBot.model.ContactModel;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final ContactDAO contactDAO;
    private final ModelMapper modelMapper;
    @Autowired
    public CustomerService(CustomerDAO customerDAO, ContactDAO contactDAO, ModelMapper modelMapper) {
        this.customerDAO = customerDAO;
        this.contactDAO = contactDAO;
        this.modelMapper = modelMapper;
    }

    public void save(User user){
        Optional<Customer> one = findOne(user.getId());
        if(one.isEmpty()){
            customerDAO.save(toCustomer(user));
            log.info("{}: новый пользователь  сохранен в БД", user.getId());}
        else{
            customerDAO.update(toCustomer(user));
            log.info("{}: данные пользователя  обновлены в БД", user.getId());
        }
    }


    public Optional<Customer> findOne(Long userId){
        return customerDAO.findOne(userId);
    }
    public Optional<Customer> findOneWithContacts(Long userId){
        return customerDAO.findOneWithContacts(userId);
    }
    public void remove(User user){
        customerDAO.deleteById(user.getId());
    }
    public List<Customer> findAll(){
        return customerDAO.findAll();
    }
    //@Transactional
    public void saveContact(ContactModel contact){
        Optional<Customer> one = customerDAO.findOneWithContacts(contact.getUserId());
        if(one.isPresent()){
            ContactEntity contactEntity = toContactEntity(contact);
            contactEntity.setCustomer(one.get());
            contactDAO.save(contactEntity);
        }
        log.info("{}: новый контакт  сохранен в БД", contact.getUserId());
    }
    private Customer toCustomer(User user){
        return modelMapper.map(user, Customer.class);
    }

    private ContactEntity toContactEntity(ContactModel contact){
        return modelMapper.map(contact,ContactEntity.class);
    }
}
