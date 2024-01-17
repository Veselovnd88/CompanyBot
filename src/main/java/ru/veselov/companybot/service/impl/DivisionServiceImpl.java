package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.veselov.companybot.dto.DivisionCreateDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.exception.DivisionAlreadyExistsException;
import ru.veselov.companybot.exception.DivisionNotFoundException;
import ru.veselov.companybot.exception.util.ExceptionMessageUtils;
import ru.veselov.companybot.mapper.DivisionMapper;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.service.DivisionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository;

    private final DivisionMapper divisionMapper;

    @Override
    @Cacheable(value = "division")
    public List<DivisionModel> findAll() {
        return divisionMapper.toListModel(divisionRepository.findAll());
    }

    @CacheEvict(value = "division")
    @Override
    public DivisionModel save(DivisionCreateDTO division) {
        String name = division.getName();
        Optional<DivisionEntity> optionalDivision = divisionRepository.findByName(name);
        if (optionalDivision.isPresent()) {
            log.warn(ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(name));
            throw new DivisionAlreadyExistsException(ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(name));
        }
        DivisionEntity saved = divisionRepository.save(divisionMapper.dtoToEntity(division));
        log.info("Division saved with [id: {} ]", saved.getDivisionId());
        return divisionMapper.toModel(saved);
    }

    @Override
    public DivisionModel findById(UUID divisionId) {
        DivisionEntity divisionEntity = divisionRepository.findById(divisionId)
                .orElseThrow(
                        () -> {
                            log.warn("Division with [id: {}] not found", divisionId);
                            return new DivisionNotFoundException("Division with [id: %s] not found"
                                    .formatted(divisionId));
                        }
                );
        log.info("Division with [id: {}] retrieved from repo", divisionId);
        return divisionMapper.toModel(divisionEntity);
    }

    @Override
    public void remove(DivisionModel division) {
        divisionRepository.deleteById(division.getDivisionId());
        log.info("Division with [id: {}] successfully removed", division.getDivisionId());
    }

}
