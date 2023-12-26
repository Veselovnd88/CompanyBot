package ru.veselov.companybot.exception;

public class UnexpectedCallbackException extends ProcessUpdateException {

    public UnexpectedCallbackException(String message, String callbackId) {
        super(message, callbackId);
    }

}
