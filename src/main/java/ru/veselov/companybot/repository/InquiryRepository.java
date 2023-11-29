package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.companybot.entity.InquiryEntity;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository extends JpaRepository<InquiryEntity, UUID> {

    @Query("SELECT i FROM InquiryEntity i LEFT JOIN FETCH i.messages WHERE i.inquiryId= :id")
    Optional<InquiryEntity> findByIdWithMessages(@Param("id") UUID id);

}
