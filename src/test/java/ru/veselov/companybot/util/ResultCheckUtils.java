package ru.veselov.companybot.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.companybot.exception.handler.ErrorCode;
import ru.veselov.companybot.exception.handler.ErrorMessage;

public class ResultCheckUtils {

    public static final String JSON_ERROR_CODE = "$.errorCode";

    public static final String JSON_TIMESTAMP = "$.timestamp";

    public static final String JSON_VIOLATIONS_FIELD = "$.violations[%s].name";

    public static final String JSON_TITLE = "$.title";

    public static final String JSON_DETAIL = "$.detail";


    public static void checkWrongTypeFields(ResultActions resultActions) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_TITLE)
                        .value(ErrorMessage.WRONG_ARGUMENT_PASSED))
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.BAD_REQUEST.toString()));
    }

    public static void checkCommonValidationFields(ResultActions resultActions) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.VALIDATION.toString()));
    }

    public static void checkNotFoundFields(ResultActions resultActions) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.NOT_FOUND.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_TIMESTAMP).isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_TITLE).isNotEmpty());
    }

    private ResultCheckUtils() {
        throw new AssertionError("not instances");
    }

}
