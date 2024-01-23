package ru.veselov.companybot.service;

import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.model.DivisionModel;

import java.util.List;
import java.util.UUID;

public interface DivisionService {
    List<DivisionModel> findAll();

    DivisionModel save(DivisionDTO division);

    DivisionModel findById(UUID divisionId);

    DivisionModel update(UUID divisionId, DivisionDTO divisionDTO);

    void delete(UUID divisionId);

}
