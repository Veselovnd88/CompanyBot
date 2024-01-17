package ru.veselov.companybot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.companybot.dto.DivisionCreateDTO;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.DivisionService;

@RestController
@RequestMapping("/api/v1/division")
@RequiredArgsConstructor
@Validated
public class DivisionController {

    private final DivisionService divisionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionModel addDivision(DivisionCreateDTO divisionDTO) {
        return divisionService.save(divisionDTO);
    }

}
