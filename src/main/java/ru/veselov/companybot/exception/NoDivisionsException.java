package ru.veselov.companybot.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoDivisionsException extends RuntimeException {
    public NoDivisionsException(String message) {
        super(message);
    }


}
