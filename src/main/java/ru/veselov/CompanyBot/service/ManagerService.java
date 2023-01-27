package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.veselov.CompanyBot.dao.ManagerDAO;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;
import ru.veselov.CompanyBot.exception.NoSuchManagerException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManagerService {

    private final ModelMapper modelMapper;

    private final ManagerDAO managerDAO;


    public ManagerService(ModelMapper modelMapper, ManagerDAO managerDAO) {
        this.modelMapper = modelMapper;
        this.managerDAO = managerDAO;
    }

    public void save(ManagerModel manager){
        ManagerEntity entity = toManagerEntity(manager);
        managerDAO.saveWithDivisions(entity,
                    manager.getDivisions().stream()
                            .map(this::toDivisionEntity).collect(Collectors.toSet()));
        log.info("{}: сохранен/обновлен менеджер с набором отделов {}", manager.getManagerId()
                    ,manager.getDivisions());
    }

    public ManagerModel findOne(Long userId) throws NoSuchManagerException {
        Optional<ManagerEntity> one = managerDAO.findOne(userId);
        if(one.isPresent()){
            return toManagerModel(one.get());
        }
        else{
            throw new NoSuchManagerException();
        }
    }

    public ManagerModel findOneWithDivisions(Long userId) throws NoSuchManagerException {
        Optional<ManagerEntity> oneWithDivisions = managerDAO.findOneWithDivisions(userId);
        if(oneWithDivisions.isPresent()){
            ManagerModel managerModel = toManagerModel(oneWithDivisions.get());
            managerModel.setDivisions(oneWithDivisions.get().getDivisions().stream().map(this::toDivisionModel)
                    .collect(Collectors.toSet()));
            return managerModel;
        }
        else throw new NoSuchManagerException();
    }

    public void remove(ManagerModel managerModel){
        log.info("{}: менеджер удален из БД", managerModel.getManagerId());
        managerDAO.deleteById(managerModel.getManagerId());
    }

    public void removeDivisions(ManagerModel managerModel){
        log.info("{}: удалены все отделы у менеджера", managerModel.getManagerId());
        managerDAO.removeDivisions(toManagerEntity(managerModel));
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
    private Division toDivisionEntity(DivisionModel divisionModel){
        Division division = new Division();
        division.setDivisionId(divisionModel.getDivisionId());
        division.setName(divisionModel.getName());
        return division;
    }
    private DivisionModel toDivisionModel(Division division){
        return DivisionModel.builder().divisionId(division.getDivisionId()).name(division.getName()).build();
    }

}
