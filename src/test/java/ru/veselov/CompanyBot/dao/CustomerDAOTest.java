package ru.veselov.CompanyBot.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Customer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class CustomerDAOTest {
    @Autowired
    private CustomerDAO customerDAO;
    @Test
    @Transactional
    void DAOTest() {
        Customer customer = new Customer();
        customer.setName("Test");
        Customer save = customerDAO.save(customer);
        Optional<Customer> byId = customerDAO.getById(save.getId());
        assertTrue(byId.isPresent());
        assertEquals(save.getId(),byId.get().getId());
    }
}