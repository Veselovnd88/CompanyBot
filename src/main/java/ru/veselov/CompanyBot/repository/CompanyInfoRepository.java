package ru.veselov.CompanyBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.veselov.CompanyBot.entity.CompanyInfoEntity;

import java.util.List;


public interface CompanyInfoRepository extends JpaRepository<CompanyInfoEntity, Long> {

    @Query("SELECT c FROM  CompanyInfoEntity c ORDER BY c.id DESC LIMIT 1")
    List<CompanyInfoEntity> findLast();//FIXME TEST ME

}
