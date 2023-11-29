package ru.veselov.companybot.service;

import ru.veselov.companybot.model.DivisionModel;

import java.util.List;
import java.util.UUID;

public interface DivisionService {
    List<DivisionModel> findAll();

    void save(DivisionModel division);

    DivisionModel findById(UUID divisionId);

    void remove(DivisionModel division);

}
