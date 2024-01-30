package ru.veselov.companybot.it.controller;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.veselov.companybot.config.BotMocks;
import ru.veselov.companybot.config.EnableTestContainers;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.exception.util.ExceptionMessageUtils;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.util.MockMvcUtils;
import ru.veselov.companybot.util.ResultCheckUtils;
import ru.veselov.companybot.util.TestUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@Import({BotMocks.class})
@ActiveProfiles("test")
@EnableTestContainers
class DivisionControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DivisionRepository divisionRepository;

    @AfterEach
    void clear() {
        divisionRepository.deleteAll();
    }

    //ADD
    @Test
    @SneakyThrows
    void addDivision_CorrectDto_SaveDivisionAndReturnSaved() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.createDivision(divisionDTO));

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

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.createDivision(divisionDTO));

        ResultCheckUtils.checkConflictError(resultActions,
                ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted(divisionDTO.getName()));
    }

    //UPDATE
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
                .perform(MockMvcUtils.updateDivision(divisionDTO, savedDivision.getDivisionId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.description", Matchers.is("New Description")))
                .andExpect(jsonPath("$.name", Matchers.is(divisionDTO.getName())))
                .andExpect(jsonPath("$.divisionId", Matchers.is(savedDivision.getDivisionId().toString())));
    }

    @Test
    @SneakyThrows
    void updateDivision_NoDivision_HandleExceptionAndReturnNotFound() {
        ResultActions resultActions = mockMvc
                .perform(MockMvcUtils.updateDivision(TestUtils.getDivisionDTO(), TestUtils.DIVISION_ID));

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
                .perform(MockMvcUtils.updateDivision(divisionDTO, savedDivision.getDivisionId()));

        ResultCheckUtils.checkConflictError(resultActions,
                ExceptionMessageUtils.DIVISION_ALREADY_EXISTS.formatted("exists"));
    }

    //DELETE
    @Test
    @SneakyThrows
    void deleteDivision_AllOk_ReturnNoContent() {
        DivisionEntity division = TestUtils.getDivisionEntity();
        DivisionEntity savedDivision = divisionRepository.saveAndFlush(division);

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.deleteDivision(savedDivision.getDivisionId()));

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteDivision_NoDivision_HandleErrorAndReturnNotFound() {
        ResultActions resultActions = mockMvc.perform(MockMvcUtils.deleteDivision(TestUtils.DIVISION_ID));

        ResultCheckUtils.checkNotFoundFields(
                resultActions, ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(TestUtils.DIVISION_ID));
    }

    @Test
    @SneakyThrows
    void getDivisionById_AllOk_ReturnDto() {
        DivisionEntity division = TestUtils.getDivisionEntity();
        DivisionEntity savedDivision = divisionRepository.saveAndFlush(division);

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.getDivision(savedDivision.getDivisionId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.description", Matchers.is(savedDivision.getDescription())))
                .andExpect(jsonPath("$.name", Matchers.is(savedDivision.getName())))
                .andExpect(jsonPath("$.divisionId", Matchers.is(savedDivision.getDivisionId().toString())));
    }

    @Test
    @SneakyThrows
    void getDivisionById_DivisionNotFound_ReturnNotFound() {
        ResultActions resultActions = mockMvc.perform(MockMvcUtils.getDivision(TestUtils.DIVISION_ID));

        resultActions.andExpect(status().isNotFound());
        ResultCheckUtils.checkNotFoundFields(resultActions,
                ExceptionMessageUtils.DIVISION_NOT_FOUND.formatted(TestUtils.DIVISION_ID));
    }

    @Test
    @SneakyThrows
    void getDivisions_AllOk_ReturnDto() {
        DivisionEntity division = TestUtils.getDivisionEntity();
        DivisionEntity savedDivision = divisionRepository.saveAndFlush(division);

        ResultActions resultActions = mockMvc.perform(MockMvcUtils.getDivisions());

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[*].description", Matchers.contains(savedDivision.getDescription())))
                .andExpect(jsonPath("$[*].name", Matchers.contains(savedDivision.getName())))
                .andExpect(jsonPath("$[*].divisionId", Matchers.contains(savedDivision.getDivisionId().toString())));
    }

}
