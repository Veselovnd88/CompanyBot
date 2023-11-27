package ru.veselov.CompanyBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.CustomerEntity;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findOneWithContacts(Long id);

}
