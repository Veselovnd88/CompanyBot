package ru.veselov.companybot.exception.handler;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.veselov.companybot.exception.ObjectAlreadyExistsException;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
@Slf4j
@ApiResponse(responseCode = "400", description = "Валидация полей объекта не прошла",
        content = @Content(
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(value = """
                        {
                           "type": "about:blank",
                           "title": "Validation error",
                           "status": 400,
                           "detail": "Validation failed",
                           "instance": "/api/v1/division",
                           "timestamp": "2024-01-20T13:55:59.666535500Z",
                           "errorCode": "VALIDATION",
                           "violations": [
                             {
                               "name": "name",
                               "message": "размер должен находиться в диапазоне от 0 до 10",
                               "currentValue": "Commonfasdfasdfasdfasdfasdfdf"
                             }
                           ]
                         }
                         """),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
@ApiResponse(responseCode = "409", description = "Отдел с таким наименованием уже существует",
        content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                        {
                          "type": "about:blank",
                          "title": "Object already exists",
                          "status": 409,
                          "detail": "Object with name COMMON already exists",
                          "instance": "/api/v1/????",
                          "timestamp": "2024-01-20T13:57:29.576908600Z",
                          "errorCode": "CONFLICT"
                        }
                        """),
                schema = @Schema(implementation = ProblemDetail.class))
        })
public class RestExceptionHandler {
    private static final String LOG_MSG_DETAILS = "[Exception {} with message {}] handled";

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.NOT_FOUND, e);
        problemDetail.setTitle(ErrorMessage.OBJECT_NOT_FOUND);
        problemDetail.setProperty(ErrorMessage.ERROR_CODE, ErrorCode.NOT_FOUND.toString());
        log.debug(LOG_MSG_DETAILS, e.getClass(), e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExistsException(ObjectAlreadyExistsException e) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.CONFLICT, e);
        problemDetail.setTitle(ErrorMessage.OBJECT_ALREADY_EXISTS);
        problemDetail.setProperty(ErrorMessage.ERROR_CODE, ErrorCode.CONFLICT.toString());
        log.debug(LOG_MSG_DETAILS, e.getClass(), e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        List<ViolationError> violationErrors = e.getConstraintViolations().stream()
                .map(v -> new ViolationError(
                        fieldNameFromPath(v.getPropertyPath().toString()),
                        v.getMessage(),
                        formatValidationCurrentValue(v.getInvalidValue())))
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        ProblemDetail configuredProblemDetails = setUpValidationDetails(problemDetail, violationErrors);
        log.debug(LOG_MSG_DETAILS, e.getClass(), e.getMessage());
        return configuredProblemDetails;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, e);
        problemDetail.setTitle(ErrorMessage.WRONG_ARGUMENT_PASSED);
        problemDetail.setProperty(ErrorMessage.ERROR_CODE, ErrorCode.BAD_REQUEST.toString());
        log.debug(LOG_MSG_DETAILS, e.getClass(), e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ViolationError> violationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ViolationError(
                        error.getField(), error.getDefaultMessage(),
                        formatValidationCurrentValue(error.getRejectedValue())))
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty(ErrorMessage.TIMESTAMP, Instant.now());
        ProblemDetail configuredProblemDetails = setUpValidationDetails(problemDetail, violationErrors);
        log.debug(LOG_MSG_DETAILS, e.getClass(), e.getMessage());
        return configuredProblemDetails;
    }


    private ProblemDetail createProblemDetail(HttpStatus status, Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setProperty(ErrorMessage.TIMESTAMP, Instant.now());
        return problemDetail;
    }

    private ProblemDetail setUpValidationDetails(ProblemDetail problemDetail, List<ViolationError> violationErrors) {
        problemDetail.setTitle(ErrorMessage.VALIDATION_ERROR);
        problemDetail.setProperty(ErrorMessage.ERROR_CODE, ErrorCode.VALIDATION.toString());
        problemDetail.setProperty(ErrorMessage.VIOLATIONS, violationErrors);
        return problemDetail;
    }

    private String fieldNameFromPath(String path) {
        String[] split = path.split("\\.");
        if (split.length > 1) {
            return split[split.length - 1];
        }
        return path;
    }

    private String formatValidationCurrentValue(Object object) {
        if (object == null) {
            return "null";
        }
        if (object.toString().contains(object.getClass().getName())) {
            return object.getClass().getSimpleName();
        }
        return object.toString();
    }

}
