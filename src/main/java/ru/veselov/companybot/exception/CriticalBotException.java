package ru.veselov.companybot.exception;

public class CriticalBotException extends RuntimeException {

    public CriticalBotException(String message, Throwable cause) {
        super(message, cause);
    }
}
