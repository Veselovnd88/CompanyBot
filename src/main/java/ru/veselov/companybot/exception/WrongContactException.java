package ru.veselov.companybot.exception;

public class WrongContactException extends ProcessUpdateException {

    public WrongContactException(String message, String chatId) {
        super(message, chatId);
    }

}

