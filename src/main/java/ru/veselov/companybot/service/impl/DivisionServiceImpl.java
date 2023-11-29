package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.exception.DivisionNotFoundException;
import ru.veselov.companybot.mapper.DivisionMapper;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.service.DivisionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository;

    private final DivisionMapper divisionMapper;

    @Override
    public List<DivisionModel> findAll() {
        return divisionMapper.toListModel(divisionRepository.findAll());
    }

    @Override
    public void save(DivisionModel division) {
        DivisionEntity saved = divisionRepository.save(divisionMapper.toEntity(division));
        log.info("Division saved with [id: {} ]", saved.getDivisionId());
    }

    @Override
    public DivisionModel findById(UUID divisionId) {
        DivisionEntity divisionEntity = divisionRepository.findById(divisionId)
                .orElseThrow(
                        () -> {
                            log.error("Division with [id: {}] not found", divisionId);
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
