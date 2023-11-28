package ru.veselov.companybot.service;

import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.model.DivisionModel;

import java.util.List;

public interface DivisionService {
    List<DivisionModel> findAll();

    void save(DivisionModel division);

    DivisionModel findById(Long divisionId) throws NoSuchDivisionException;

    void remove(DivisionModel division);

}
