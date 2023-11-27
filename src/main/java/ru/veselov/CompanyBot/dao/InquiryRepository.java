package ru.veselov.CompanyBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

}
