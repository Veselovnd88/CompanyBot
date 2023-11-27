package ru.veselov.CompanyBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

}
