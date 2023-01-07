package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.dao.CustomerDAO;
import ru.veselov.CompanyBot.entity.Customer;

import java.util.Optional;

@Service
@Slf4j
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final ModelMapper modelMapper;
    @Autowired
    public CustomerService(CustomerDAO customerDAO, ModelMapper modelMapper) {
        this.customerDAO = customerDAO;
        this.modelMapper = modelMapper;
    }

    public void save(User user){
        Optional<Customer> one = findOne(user.getId());
        if(one.isEmpty()){
            customerDAO.save(toCustomer(user));}
        else{
            customerDAO.update(toCustomer(user));
        }
    }

    public Optional<Customer> findOne(Long userId){
        return customerDAO.findOne(userId);
    }

    private Customer toCustomer(User user){
        return modelMapper.map(user, Customer.class);
    }
}
