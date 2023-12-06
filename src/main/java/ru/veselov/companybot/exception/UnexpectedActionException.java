package ru.veselov.companybot.exception;

public class UnexpectedActionException extends ProcessUpdateException {

    public UnexpectedActionException(String message, String chatId) {
        super(message, chatId);
    }

}
