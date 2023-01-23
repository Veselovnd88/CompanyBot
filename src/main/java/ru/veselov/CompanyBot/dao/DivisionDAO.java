package ru.veselov.CompanyBot.dao;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class DivisionDAO {
    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public DivisionDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public List<Division> findAll(){
        return entityManager.createQuery(" SELECT d from Division d ").getResultList();
    }

    @Transactional
    public void save(Division division){
        Optional<Division> oneWithManagers = findOneWithManagers(division.getDivisionId());
        if(oneWithManagers.isEmpty()){
            findOneWithManagers(division.getDivisionId());
            entityManager.persist(division);}
        else{
            entityManager.merge(division);
        }


    }
    @SuppressWarnings("unchecked")
    public Optional<Division> findByName(String name){
        Query query = entityManager.createQuery("select d from Division d where name = :param");
        query.setParameter("param",name);
        List<Division> resultList = query.getResultList();
        return resultList.stream().findFirst();
    }

    public Optional<Division> findOne(String id){
        //Стандартный тип инициализации - Lazy - не получает привязанные к нему Inquiry
        Division division = entityManager.find(Division.class,id);
        return Optional.ofNullable(division);
    }

    public Optional<Division> findOneWithManagers(String id){
        Division division = entityManager.find(Division.class,id);
        if(division!=null){
            Hibernate.initialize(division.getManagers());
        }
        return Optional.ofNullable(division);
    }


    @Transactional
    public void delete(Division division){
        entityManager.remove(division);
    }

    @Transactional
    public void deleteById(String id){
        Optional<Division> division = findOneWithManagers(id);
        if(division.isPresent()){
            for(ManagerEntity me: division.get().getManagers()){
                me.removeDivision(division.get());
            }
            delete(division.get());
        }
    }
}
