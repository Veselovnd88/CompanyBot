package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.companybot.dto.DivisionDTO;
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
@Transactional(readOnly = true)
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository;

    private final DivisionMapper divisionMapper;

    @Override
    @Cacheable(value = "division")
    public List<DivisionModel> findAll() {
        return divisionMapper.toListModel(divisionRepository.findAll());
    }

    @CacheEvict(value = "division", allEntries = true)
    @Override
    @Transactional
    public DivisionModel save(DivisionDTO division) {
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
                            log.warn(ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(divisionId));
                            return new DivisionNotFoundException(ExceptionMessageUtils.DIVISION_NOT_FOUND
                                    .formatted(divisionId));
                        }
                );
        log.debug("Division with [id: {}] retrieved from repo", divisionId);
        return divisionMapper.toModel(divisionEntity);
    }

    @CacheEvict(value = "division", allEntries = true)
    @Override
    @Transactional
    public DivisionModel update(UUID divisionId, DivisionDTO divisionDTO) {
        DivisionEntity divisionEntity = divisionRepository.findById(divisionId)
                .orElseThrow(
                        () -> {
                            log.warn(ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(divisionId));
                            return new DivisionNotFoundException(ExceptionMessageUtils.DIVISION_NOT_FOUND
                                    .formatted(divisionId));
                        }
                );
        String name = divisionDTO.getName();
        if (!divisionEntity.getName().equals(name)) {
            Optional<DivisionEntity> optionalDivision = divisionRepository.findByName(name);
            if (optionalDivision.isPresent()) {
                log.warn(ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(name));
                throw new DivisionAlreadyExistsException(ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(name));
            }
        }
        divisionEntity.setName(name);
        divisionEntity.setDescription(divisionDTO.getDescription());
        DivisionEntity saved = divisionRepository.save(divisionEntity);
        log.info("Division with [id: {}] successfully updated", divisionId);
        return divisionMapper.toModel(saved);
    }

    @CacheEvict(value = "division", allEntries = true)
    @Override
    @Transactional
    public void delete(UUID divisionId) {
        if (!divisionRepository.existsById(divisionId)) {
            log.warn(ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(divisionId));
            throw new DivisionNotFoundException(ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(divisionId));
        }
        divisionRepository.deleteById(divisionId);
        log.info("Division with [id: {}] successfully removed", divisionId);
    }

}
