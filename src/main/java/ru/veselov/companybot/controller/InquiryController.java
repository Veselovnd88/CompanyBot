package ru.veselov.companybot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.companybot.annotation.PagingParam;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.dto.PagingParams;
import ru.veselov.companybot.service.InquiryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
@Validated
@Tag(name = "Полученные ботом запросы от клиентов", description = "Просмотр запросов")
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "Получить все запросы",
            description = "Выгружает все запросы",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page"),
                    @Parameter(in = ParameterIn.QUERY, name = "size")})
    @ApiResponse(responseCode = "200", description = "Запросы успешно получены",
            content = {@Content(array = @ArraySchema(schema = @Schema(implementation = InquiryResponseDTO.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @GetMapping
    public Page<InquiryResponseDTO> getAll(@Schema(hidden = true) @Valid @PagingParam PagingParams pagingParams) {
        return inquiryService.findAll(pagingParams);
    }

}
