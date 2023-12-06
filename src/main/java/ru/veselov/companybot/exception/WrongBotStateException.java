package ru.veselov.companybot.exception;

public class WrongBotStateException extends ProcessUpdateException {

    public WrongBotStateException(String message, String chatId) {
        super(message, chatId);
    }

}
