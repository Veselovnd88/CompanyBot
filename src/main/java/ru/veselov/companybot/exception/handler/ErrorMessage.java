package ru.veselov.companybot.exception.handler;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessage {

    public static final String ERROR_CODE = "errorCode";

    public static final String TIMESTAMP = "timestamp";

    public static final String VALIDATION_ERROR = "Validation error";

    public static final String VIOLATIONS = "violations";

    public static final String OBJECT_NOT_FOUND = "Object not found";

    public static final String OBJECT_ALREADY_EXISTS = "Object already exists";

    public static final String WRONG_ARGUMENT_PASSED = "Wrong type of argument passed";

}
