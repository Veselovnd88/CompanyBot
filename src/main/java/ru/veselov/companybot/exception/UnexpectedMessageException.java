package ru.veselov.companybot.exception;

public class UnexpectedMessageException extends ProcessUpdateException {

    public UnexpectedMessageException(String message, String chatId) {
        super(message, chatId);
    }
}
