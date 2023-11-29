package ru.veselov.companybot.exception;

import lombok.Getter;

@Getter
public class NoAvailableActionException extends RuntimeException {

    private final String chatId;

    public NoAvailableActionException(String message, String chatId) {
        super(message);
        this.chatId = chatId;
    }

    public NoAvailableActionException(String message, String chatId, Exception cause) {
        super(message, cause);
        this.chatId = chatId;
    }


}
