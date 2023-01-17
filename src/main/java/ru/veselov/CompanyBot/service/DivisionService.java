package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.veselov.CompanyBot.dao.DivisionDAO;
import ru.veselov.CompanyBot.entity.Division;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DivisionService{
    private final DivisionDAO divisionDAO;
    @Autowired
    public DivisionService(DivisionDAO divisionDAO) {
        this.divisionDAO = divisionDAO;
    }

    public List<Division> findAll(){
        return divisionDAO.findAll();
    }

    public void save(Division division){
        if(divisionDAO.findOne(division.getDivisionId()).isPresent()){
            divisionDAO.update(division);
        }
        else{
            divisionDAO.save(division);
        }
    }

    public Optional<Division> findOneWithManagers(Division division){
        return divisionDAO.findOneWithManagers(division.getDivisionId());
    }

    public Optional<Division> findOne(Division division){
        return divisionDAO.findOne(division.getDivisionId());
    }




}
