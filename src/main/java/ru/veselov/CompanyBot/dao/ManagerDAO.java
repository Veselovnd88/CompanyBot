package ru.veselov.CompanyBot.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.ChatEntity;
import ru.veselov.CompanyBot.entity.Customer;
import ru.veselov.CompanyBot.entity.ManagerEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@Slf4j
public class ManagerDAO {

    @PersistenceContext
    private final EntityManager entityManager;

    public ManagerDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public ManagerEntity save(ManagerEntity managerEntity){
        entityManager.persist(managerEntity);
        return managerEntity;
    }
    public List<ChatEntity> findAll(){
        return entityManager.createQuery(" SELECT m from ManagerEntity m ").getResultList();
    }
    public Optional<ManagerEntity> findOne(Long managerId){
        ManagerEntity manager= entityManager.find(ManagerEntity.class, managerId);
        return Optional.ofNullable(manager);
    }
    @Transactional
    public ManagerEntity update(ManagerEntity manager){
        return entityManager.merge(manager);
    }


    public Optional<ManagerEntity> findOneWithDivisions(Long managerId){
        ManagerEntity manager = entityManager.find(ManagerEntity.class,managerId);
        if(manager!=null){
            Hibernate.initialize(manager.getDivisions());
        }
        return Optional.ofNullable(manager);
    }
    @Transactional
    public void delete(ManagerEntity manager){
        entityManager.remove(manager);
    }

    @Transactional
    public void deleteById(Long managerId) {
        Optional<ManagerEntity> manager = findOne(managerId);
        manager.ifPresent(this::delete);
    }

}
