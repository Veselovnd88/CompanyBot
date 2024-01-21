package ru.veselov.companybot.util;

import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.companybot.exception.handler.ErrorCode;
import ru.veselov.companybot.exception.handler.ErrorMessage;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    public static void checkNotFoundFields(ResultActions resultActions, String detail) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.NOT_FOUND.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_TIMESTAMP).isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath(ResultCheckUtils.JSON_TITLE).isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath(JSON_DETAIL, Matchers.is(detail)));
    }

    public static void checkConflictError(ResultActions resultActions, String detail) throws Exception {
        resultActions.andExpect(status().isConflict())
                .andExpect(jsonPath(ResultCheckUtils.JSON_TITLE, Matchers.is(ErrorMessage.OBJECT_ALREADY_EXISTS)))
                .andExpect(jsonPath(ResultCheckUtils.JSON_DETAIL,
                        Matchers.is(detail)));
    }

    private ResultCheckUtils() {
        throw new AssertionError("not instances");
    }

}
