package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.companybot.entity.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

}
