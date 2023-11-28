package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.companybot.entity.Inquiry;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Optional<Inquiry> findOneWithManagers(Long divisionId);

}
