package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.companybot.entity.DivisionEntity;

import java.util.Optional;
import java.util.UUID;

public interface DivisionRepository extends JpaRepository<DivisionEntity, UUID> {

    @Query("SELECT d from DivisionEntity d WHERE d.name = :name")
    Optional<DivisionEntity> findByName(@Param("name") String name);

}
