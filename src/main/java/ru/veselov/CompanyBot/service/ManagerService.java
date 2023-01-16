package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public ManagerService(ModelMapper modelMapper, ManagerDAO managerDAO) {
        this.modelMapper = modelMapper;
        this.managerDAO = managerDAO;
    }

    public void save(User user){
        Optional<ManagerEntity> one = findOne(user.getId());
        if(one.isEmpty()){
            managerDAO.save(toEntity(user));
            log.info("{}: новый менеджер сохранен в БД", user.getId());}
        else{
            managerDAO.update(toEntity(user));
            log.info("{} данные менеджера обновлены в БД", user.getId());
        }
    }

    public void saveWithDivisions(User user, Set<Division> divs){
        Optional<ManagerEntity> one = findOne(user.getId());
        ManagerEntity managerEntity = toEntity(user);
        managerEntity.setDivisions(divs);
        if(one.isEmpty()){
            managerDAO.save(managerEntity);
            log.info("{}: сохранен менеджер с набором отделов", user.getId());
        }
        else{
            managerDAO.update(managerEntity);
            log.info("{}: обновлен менеджер с набором отделов", user.getId());
        }
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


    private ManagerEntity toEntity(User user){
        ManagerEntity mapped = modelMapper.map(user, ManagerEntity.class);
        mapped.setManagerId(user.getId());
        return mapped;
    }

}
