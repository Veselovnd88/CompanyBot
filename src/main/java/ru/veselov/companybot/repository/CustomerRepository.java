package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.companybot.entity.CustomerEntity;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findOneWithContacts(Long id);

}
