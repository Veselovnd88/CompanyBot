package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.veselov.CompanyBot.dao.DivisionDAO;
import ru.veselov.CompanyBot.entity.Division;

import java.util.List;

@Service
@Slf4j
public class DivisionService{
    private final DivisionDAO dao;
    @Autowired
    public DivisionService(DivisionDAO dao) {
        this.dao = dao;
    }

    public List<Division> findAll(){
        return dao.findAll();
    }

    public void save(Division division){
        if(dao.findByName(division.getName()).isEmpty()){
            dao.save(division);
        }
    }




}
