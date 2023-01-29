package ru.veselov.CompanyBot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.entity.CompanyInfoEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CompanyInfoDao {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public CompanyInfoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @SuppressWarnings("unchecked")
    public List<CompanyInfoEntity> findLast(){
        return entityManager.createQuery(
                "select c from  CompanyInfoEntity c order by c.id desc").setMaxResults(1).getResultList();
    }
    @Transactional
    public void save(CompanyInfoEntity companyInfoEntity){
        entityManager.persist(companyInfoEntity);
    }
}
