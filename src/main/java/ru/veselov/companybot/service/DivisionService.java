package ru.veselov.companybot.service;

import ru.veselov.companybot.dto.DivisionCreateDTO;
import ru.veselov.companybot.model.DivisionModel;

import java.util.List;
import java.util.UUID;

public interface DivisionService {
    List<DivisionModel> findAll();

    DivisionModel save(DivisionCreateDTO division);

    DivisionModel findById(UUID divisionId);

    void remove(DivisionModel division);

}
