package ru.veselov.companybot.exception;

import lombok.Getter;

@Getter
public class ProcessUpdateException extends RuntimeException {

    private final String chatId;

    public ProcessUpdateException(String message, String chatId) {
        super(message);
        this.chatId = chatId;
    }

    public ProcessUpdateException(String message, String chatId, Exception cause) {
        super(message, cause);
        this.chatId = chatId;
    }

}
