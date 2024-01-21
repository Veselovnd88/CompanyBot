package ru.veselov.companybot.it.controller;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.veselov.companybot.config.BotMocks;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.exception.util.ExceptionMessageUtils;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.util.RestUrl;
import ru.veselov.companybot.util.ResultCheckUtils;
import ru.veselov.companybot.util.TestUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@Import({BotMocks.class})
@ActiveProfiles("test")
class DivisionControllerIntegrationTest extends PostgresTestContainersConfiguration {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DivisionRepository divisionRepository;

    @AfterEach
    void clear() {
        divisionRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void addDivision_CorrectDto_SaveDivisionAndReturnSaved() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(RestUrl.DIVISION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.jsonStringFromObject(divisionDTO)));

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.divisionId").isNotEmpty())
                .andExpect(jsonPath("$.name", Matchers.is(divisionDTO.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(divisionDTO.getDescription())));
    }

    @Test
    @SneakyThrows
    void addDivision_NameExists_HandleErrorAndReturnConflict() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        DivisionEntity divisionEntity = DivisionEntity.builder()
                .name(divisionDTO.getName()).description(divisionDTO.getDescription())
                .build();
        divisionRepository.saveAndFlush(divisionEntity);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(RestUrl.DIVISION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.jsonStringFromObject(divisionDTO)));

        ResultCheckUtils.checkConflictError(resultActions,
                ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(divisionDTO.getName()));
    }

    @Test
    @SneakyThrows
    void updateDivision_AllOk_UpdateAndReturnUpdatedDivision() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        DivisionEntity divisionEntity = DivisionEntity.builder()
                .name(divisionDTO.getName()).description(divisionDTO.getDescription())
                .build();
        DivisionEntity savedDivision = divisionRepository.saveAndFlush(divisionEntity);
        divisionDTO.setDescription("New Description");

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.put(RestUrl.DIVISION + "/" + savedDivision.getDivisionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.jsonStringFromObject(divisionDTO)));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.description", Matchers.is("New Description")))
                .andExpect(jsonPath("$.name", Matchers.is(divisionDTO.getName())))
                .andExpect(jsonPath("$.divisionId", Matchers.is(savedDivision.getDivisionId().toString())));
    }

    @Test
    @SneakyThrows
    void updateDivision_NoDivision_HandleExceptionAndReturnNotFound() {
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.put(RestUrl.DIVISION + "/" + TestUtils.DIVISION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.jsonStringFromObject(TestUtils.getDivisionDTO())));

        ResultCheckUtils.checkNotFoundFields(resultActions,
                ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(TestUtils.DIVISION_ID));
    }

    @Test
    @SneakyThrows
    void updateDivision_TryToChangeNameToExisting_HandleExceptionAndReturnConflict() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        DivisionEntity divisionEntity = DivisionEntity.builder()
                .name(divisionDTO.getName()).description(divisionDTO.getDescription())
                .build();
        DivisionEntity savedDivision = divisionRepository.saveAndFlush(divisionEntity);
        DivisionEntity secondDivision = divisionRepository.saveAndFlush(DivisionEntity.builder()
                .name("exists").description("descr").build());
        divisionDTO.setName(secondDivision.getName());

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.put(RestUrl.DIVISION + "/" + savedDivision.getDivisionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.jsonStringFromObject(divisionDTO)));

        ResultCheckUtils.checkConflictError(resultActions,
                ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted("exists"));
    }

}
