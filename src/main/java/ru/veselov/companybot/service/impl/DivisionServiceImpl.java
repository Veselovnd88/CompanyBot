package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.entity.ManagerEntity;
import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.service.DivisionService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository;

    @Override
    public List<DivisionModel> findAll() {
        return divisionRepository.findAll().stream().map(this::toDivisionModel).toList();
    }

    @Override
    public void save(DivisionModel division) {
        log.info("{}:отдел сохранен/обновлен", division.getDivisionId());
        divisionRepository.save(toDivisionEntity(division));
    }

    @Override
    public DivisionModel findOneWithManagers(DivisionModel division) throws NoSuchDivisionException {
        Optional<DivisionEntity> oneWithManagers = divisionRepository.findOneWithManagers(division.getDivisionId());
        if (oneWithManagers.isPresent()) {
            DivisionModel divisionModel = toDivisionModel(oneWithManagers.get());
        } else throw new NoSuchDivisionException();
    }

    @Override
    public DivisionModel findOne(DivisionModel division) throws NoSuchDivisionException {
        Optional<DivisionEntity> one = divisionRepository.findById(division.getDivisionId());
        if (one.isPresent()) {
            return toDivisionModel(one.get());
        } else throw new NoSuchDivisionException();
    }

    @Override
    public void remove(DivisionModel division) {
        divisionRepository.deleteById(division.getDivisionId());
        log.info("{}: отдел удален", division.getDivisionId());
    }


    private DivisionEntity toDivisionEntity(DivisionModel divisionModel) {
        DivisionEntity divisionEntity = new DivisionEntity();
        divisionEntity.setDivisionId(divisionModel.getDivisionId());
        divisionEntity.setName(divisionModel.getName());
        return divisionEntity;
    }

    private DivisionModel toDivisionModel(DivisionEntity divisionEntity) {
        return DivisionModel.builder().divisionId(divisionEntity.getDivisionId()).name(divisionEntity.getName()).build();
    }

    private ManagerEntity toManagerEntity(ManagerModel manager) {
        ManagerEntity managerEntity = new ManagerEntity();
        managerEntity.setManagerId(manager.getManagerId());
        managerEntity.setLastName(manager.getLastName());
        managerEntity.setFirstName(manager.getFirstName());
        managerEntity.setUserName(manager.getUserName());
        return managerEntity;
    }

    private ManagerModel toManagerModel(ManagerEntity manager) {
        return ManagerModel.builder().managerId(manager.getManagerId()).userName(manager.getUserName())
                .lastName(manager.getLastName()).firstName(manager.getFirstName()).build();
    }
}
