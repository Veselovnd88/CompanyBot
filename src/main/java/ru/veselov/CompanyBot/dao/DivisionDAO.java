package ru.veselov.CompanyBot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Division;

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
    public Division save(Division division){
        entityManager.persist(division);
        return division;

    }

    public Optional<Division> findByName(String name){
        Query query = entityManager.createQuery("select d from Division d where name = :param");
        query.setParameter("param",name);
        List<Division> resultList = query.getResultList();
        return resultList.stream().findFirst();
    }

    public Optional<Division> findOne(Integer id){
        //Стандартный тип инициализации - Lazy - не получает привязанные к нему Inquiry
        Division division = entityManager.find(Division.class,id);
        return Optional.ofNullable(division);
    }

    @Transactional
    public Division update(Division division){
        return entityManager.merge(division);
    }

    @Transactional
    public void delete(Division division){
        entityManager.remove(division);
    }

    @Transactional
    public void deleteById(Integer id){
        Optional<Division> division = findOne(id);
        division.ifPresent(this::delete);
    }
}
