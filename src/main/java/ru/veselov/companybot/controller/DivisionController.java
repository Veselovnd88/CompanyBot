package ru.veselov.companybot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.DivisionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/divisions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Division, Отдел, Департамент", description = "Управление отделами")
public class DivisionController {

    private final DivisionService divisionService;

    @Operation(summary = "Добавить новый отдел",
            description = "Принимает название и описание отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отдел создан",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DivisionModel.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionModel addDivision(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = DivisionDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ))
            @RequestBody @Valid DivisionDTO divisionDTO) {
        return divisionService.save(divisionDTO);
    }

    @Operation(summary = "Получить все отделы",
            description = "Выгрузка из базы данных всех отделов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отдел успешно выгружены",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = DivisionModel.class)),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping
    public List<DivisionModel> getDivisions() {
        return divisionService.findAll();
    }

    @Operation(summary = "Получить отдел по его id",
            description = "Получение информации об отделе по его id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отдел успешно выгружены",
                    content = {@Content(schema = @Schema(implementation = DivisionModel.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping("/{divisionId}")
    public DivisionModel getDivisionById(@PathVariable UUID divisionId) {
        return divisionService.findById(divisionId);
    }

    @Operation(summary = "Обновить отдел",
            description = "Обновление наименования и описания отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отдел обновлен",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DivisionModel.class))})
    })
    @PutMapping("/{divisionId}")
    public DivisionModel updateDivision(@PathVariable UUID divisionId,
                                        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                content = @Content(schema = @Schema(implementation = DivisionDTO.class),
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE
                                                ))
                                        @RequestBody @Valid DivisionDTO divisionDTO) {
        return divisionService.update(divisionId, divisionDTO);
    }

    @Operation(summary = "Удалить отдел",
            description = "Удаление отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Отдел удален")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{divisionId}")
    public void deleteDivision(@PathVariable UUID divisionId) {
        divisionService.delete(divisionId);
    }

}
