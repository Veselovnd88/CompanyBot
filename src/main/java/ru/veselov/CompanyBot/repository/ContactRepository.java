package ru.veselov.CompanyBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

}
