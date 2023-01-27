package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.veselov.CompanyBot.dao.DivisionDAO;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;
import ru.veselov.CompanyBot.exception.NoSuchDivisionException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DivisionService{
    private final DivisionDAO divisionDAO;
    @Autowired
    public DivisionService(DivisionDAO divisionDAO) {
        this.divisionDAO = divisionDAO;
    }

    public List<DivisionModel> findAll(){
        return divisionDAO.findAll().stream().map(this::toDivisionModel).toList();
    }

    public void save(DivisionModel division){
        log.info("{}:отдел сохранен/обновлен",division.getDivisionId());
        divisionDAO.save(toDivisionEntity(division));
    }

    public DivisionModel findOneWithManagers(DivisionModel division) throws NoSuchDivisionException {
        Optional<Division> oneWithManagers = divisionDAO.findOneWithManagers(division.getDivisionId());
        if(oneWithManagers.isPresent()){
            DivisionModel divisionModel = toDivisionModel(oneWithManagers.get());
            divisionModel.setManagers(
                    oneWithManagers.get().getManagers()
                            .stream().map(this::toManagerModel).collect(Collectors.toSet()));
            return divisionModel;
        }
        else throw new NoSuchDivisionException();
    }

    public DivisionModel findOne(DivisionModel division) throws NoSuchDivisionException {
        Optional<Division> one = divisionDAO.findOne(division.getDivisionId());
        if(one.isPresent()){
            return toDivisionModel(one.get());
        }
        else throw new NoSuchDivisionException();
    }

    public void remove(DivisionModel division){
        divisionDAO.deleteById(division.getDivisionId());
        log.info("{}: отдел удален",division.getDivisionId() );
    }


    private Division toDivisionEntity(DivisionModel divisionModel){
        Division division = new Division();
        division.setDivisionId(divisionModel.getDivisionId());
        division.setName(divisionModel.getName());
        return division;
    }
    private DivisionModel toDivisionModel(Division division){
        return DivisionModel.builder().divisionId(division.getDivisionId()).name(division.getName()).build();
    }
    private ManagerEntity toManagerEntity(ManagerModel manager){
        ManagerEntity managerEntity = new ManagerEntity();
        managerEntity.setManagerId(manager.getManagerId());
        managerEntity.setLastName(manager.getLastName());
        managerEntity.setFirstName(manager.getFirstName());
        managerEntity.setUserName(manager.getUserName());
        return managerEntity;
    }
    private ManagerModel toManagerModel(ManagerEntity manager){
        return ManagerModel.builder().managerId(manager.getManagerId()).userName(manager.getUserName())
                .lastName(manager.getLastName()).firstName(manager.getFirstName()).build();
    }
}
