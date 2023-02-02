package ru.veselov.CompanyBot.dao;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Customer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CustomerDAO {
    //#FIXME в дальнейшем перенести в абстрактный класс
    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public CustomerDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @SuppressWarnings("unchecked")
    public List<Customer> findAll(){
        return entityManager.createQuery(" SELECT c from Customer c ").getResultList();
    }
    @Transactional
    public Customer save(Customer customer){
        entityManager.persist(customer);
        return customer;
    }

    public Optional<Customer> findOne(Long id){
        //Стандартный тип инициализации - Lazy - не получает привязанные к нему Inquiry
        Customer customer = entityManager.find(Customer.class,id);
        return Optional.ofNullable(customer);
    }

    public Optional<Customer> findOneWithContacts(Long id){//TODO сделать namedQuery
        Customer customer = entityManager.find(Customer.class,id);
        if(customer!=null){
            Hibernate.initialize(customer.getContacts());
        }
        return Optional.ofNullable(customer);
    }
    @Transactional
    public Customer update(Customer customer){
        return entityManager.merge(customer);
    }

    @Transactional
    public void delete(Customer customer){
        entityManager.remove(customer);
    }
    @Transactional
    public void deleteById(Long id){
        Optional<Customer> optionalCustomer = findOne(id);
        optionalCustomer.ifPresent(this::delete);
    }







}
