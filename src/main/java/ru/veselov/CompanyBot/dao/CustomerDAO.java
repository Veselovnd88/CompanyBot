package ru.veselov.CompanyBot.dao;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Customer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CustomerDAO {
    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public CustomerDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Customer save(Customer customer){
        entityManager.persist(customer);
        return customer;

    }

    public Optional<Customer> getById(Integer id){
        Customer customer = entityManager.find(Customer.class,id);
        return Optional.of(customer);
    }




}
