package ru.veselov.CompanyBot.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.Inquiry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Transactional(readOnly = true)
public class InquiryDAO {
    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public InquiryDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }



    public List<Inquiry> findAll(){
        return entityManager.createQuery(" SELECT i from Inquiry i ").getResultList();
    }
    @Transactional
    public Inquiry save(Inquiry inquiry){
        entityManager.persist(inquiry);
        return inquiry;
    }

    public Optional<Inquiry> findOne(Integer id){
        Inquiry inquiry = entityManager.find(Inquiry.class,id);
        return Optional.ofNullable(inquiry);
    }
    public Optional<Inquiry> findOneWithMessages(Integer id){
        Inquiry inquiry = entityManager.find(Inquiry.class,id);
        if(inquiry!=null){
            Hibernate.initialize(inquiry.getMessages());
        }
        //если мы просто получили поле, и ничего с ним не сделали - то гибер его все равно не загружает
        return Optional.ofNullable(inquiry);
    }

    @Transactional
    public void delete(Inquiry inquiry){
        entityManager.remove(inquiry);
    }
    @Transactional
    public void deleteById(Integer id){
        Optional<Inquiry> optionalInquiry = findOne(id);
        optionalInquiry.ifPresent(this::delete);
    }
}
