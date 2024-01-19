package ru.veselov.companybot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.DivisionService;

@RestController
@RequestMapping("/api/v1/division")
@RequiredArgsConstructor
@Validated
@Tag(name = "Division, Отдел, Департамент", description = "Управление отделами")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Отдел не найден"),
        @ApiResponse(responseCode = "409", description = "Отдел с таким названием уже существует")})
public class DivisionController {

    private final DivisionService divisionService;

    @Operation(summary = "Добавить новый отдел",
            description = "Принимает название и описание отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отдел создан",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DivisionDTO.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionModel addDivision(@RequestBody @Valid DivisionDTO divisionDTO) {
        return divisionService.save(divisionDTO);
    }

}
