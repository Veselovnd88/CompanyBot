package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.dao.ManagerDAO;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.ManagerEntity;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ManagerService {

    private final ModelMapper modelMapper;

    private final ManagerDAO managerDAO;

    private final DivisionService divisionService;

    public ManagerService(ModelMapper modelMapper, ManagerDAO managerDAO, DivisionService divisionService) {
        this.modelMapper = modelMapper;
        this.managerDAO = managerDAO;
        this.divisionService = divisionService;
    }

    public void save(User user){
        managerDAO.save(toEntity(user));
        log.info("{}: новый менеджер сохранен в БД", user.getId());
    }

    public void saveWithDivisions(User user, Set<Division> divs){
        ManagerEntity managerEntity = toEntity(user);
        managerDAO.saveWithDivisions(managerEntity,divs);
        log.info("{}: сохранен/обновлен менеджер с набором отделов {}", user.getId(),divs);
    }

    public Optional<ManagerEntity> findOne(Long userId){
        return managerDAO.findOne(userId);
    }

    public Optional<ManagerEntity> findOneWithDivisions(Long userId){
        return managerDAO.findOneWithDivisions(userId);
    }

    public void remove(User user){
        log.info("{}: менеджер удален из БД", user.getId());
        managerDAO.deleteById(user.getId());
    }

    public void removeDivisions(User user){
        log.info("{}: удалены все отделы у менеджера", user.getId());
        managerDAO.removeDivisions(toEntity(user));
    }


    private ManagerEntity toEntity(User user){
        ManagerEntity mapped = modelMapper.map(user, ManagerEntity.class);
        mapped.setManagerId(user.getId());
        return mapped;
    }

}
