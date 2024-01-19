package ru.veselov.companybot.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.veselov.companybot.service.DivisionService;

@WebMvcTest(DivisionControllerValidationTest.class)
@ActiveProfiles("test")
public class DivisionControllerValidationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DivisionService divisionService;

    @Test
    void addDivision_BadDtoPassed_HandleErrorAndReturnValidationError() {

    }
}
