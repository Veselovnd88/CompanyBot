package ru.veselov.companybot.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.companybot.exception.ObjectAlreadyExistsException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
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
