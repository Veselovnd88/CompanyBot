package ru.veselov.companybot.exception;

public class KeyBoardException extends ProcessUpdateException {
    public KeyBoardException(String message, String chatId) {
        super(message, chatId);
    }
}
