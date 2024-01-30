package ru.veselov.companybot.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.veselov.companybot.controller.DivisionController;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.service.DivisionService;
import ru.veselov.companybot.util.MockMvcUtils;
import ru.veselov.companybot.util.RestUrl;
import ru.veselov.companybot.util.ResultCheckUtils;
import ru.veselov.companybot.util.TestUtils;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DivisionController.class)
@ActiveProfiles("test")
class DivisionControllerValidationTest {

    public static final String NAME = "name";

    public static final String DESCRIPTION = "description";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DivisionService divisionService;

    @ParameterizedTest
    @MethodSource("getBadDivisionDTO")
    @SneakyThrows
    void addDivision_BadDtoPassed_HandleErrorAndReturnValidationError(DivisionDTO divisionDTO, String field) {
        ResultActions resultActions = mockMvc.perform(MockMvcUtils.createDivision(divisionDTO));

        ResultCheckUtils.checkCommonValidationFields(resultActions);
        resultActions.andExpect(
                jsonPath(ResultCheckUtils.JSON_VIOLATIONS_FIELD.formatted(0), Matchers.is(field)));
    }

    @ParameterizedTest
    @MethodSource("getBadDivisionDTO")
    @SneakyThrows
    void updateDivision_BadDtoPassed_HandleErrorAndReturnValidationError(DivisionDTO divisionDTO, String field) {
        ResultActions resultActions = mockMvc.perform(MockMvcUtils.updateDivision(divisionDTO, TestUtils.DIVISION_ID));

        ResultCheckUtils.checkCommonValidationFields(resultActions);
        resultActions.andExpect(
                jsonPath(ResultCheckUtils.JSON_VIOLATIONS_FIELD.formatted(0), Matchers.is(field)));
    }

    @ParameterizedTest
    @MethodSource("getMethodWithBadUUID")
    @SneakyThrows
    void addUpdateDelete_IfUuidNotCorrect_HandleExceptionAndReturnError(MockHttpServletRequestBuilder method) {
        ResultActions resultActions = mockMvc.perform(method);

        ResultCheckUtils.checkWrongTypeFields(resultActions);
    }

    private static Stream<Arguments> getBadDivisionDTO() {
        DivisionDTO emptyName = new DivisionDTO("", TestUtils.DIVISION_DESC);
        DivisionDTO emptyDesc = new DivisionDTO(TestUtils.DIVISION_NAME, "");
        DivisionDTO longName = new DivisionDTO("a".repeat(11), TestUtils.DIVISION_DESC);
        DivisionDTO longDesc = new DivisionDTO(TestUtils.DIVISION_NAME, "a".repeat(100));
        DivisionDTO nullName = new DivisionDTO(null, TestUtils.DIVISION_DESC);
        DivisionDTO nullDesc = new DivisionDTO(TestUtils.DIVISION_NAME, null);
        return Stream.of(
                Arguments.of(emptyName, NAME),
                Arguments.of(emptyDesc, DESCRIPTION),
                Arguments.of(longName, NAME),
                Arguments.of(longDesc, DESCRIPTION),
                Arguments.of(nullName, NAME),
                Arguments.of(nullDesc, DESCRIPTION)
        );
    }

    private static Stream<MockHttpServletRequestBuilder> getMethodWithBadUUID() throws JsonProcessingException {
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get(RestUrl.DIVISIONS + "/" + "notUUID");
        MockHttpServletRequestBuilder put = MockMvcRequestBuilders.put(RestUrl.DIVISIONS + "/" + "notUUID")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.jsonStringFromObject(TestUtils.getDivisionDTO()));
        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(RestUrl.DIVISIONS + "/" + "notUUID");
        return Stream.of(get, put, delete);
    }

}
