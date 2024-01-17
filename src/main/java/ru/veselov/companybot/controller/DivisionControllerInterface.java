package ru.veselov.companybot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.model.DivisionModel;

public interface DivisionControllerInterface {

    @Operation(summary = "Добавить новый отдел",
            description = "Принимает название и описание отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отдел создан",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DivisionDTO.class))})
    })
    DivisionModel addDivision(DivisionDTO divisionDTO);

}
