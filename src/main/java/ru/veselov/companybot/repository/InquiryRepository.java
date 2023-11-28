package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.companybot.entity.Inquiry;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("SELECT i FROM Inquiry i LEFT JOIN FETCH i.messages WHERE i.inquiryId= :id")
    Optional<Inquiry> findByIdWithMessages(@Param("id") Long id);

}
