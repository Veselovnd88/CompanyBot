package ru.veselov.companybot.exception;

import lombok.Getter;

public class NoAvailableActionException extends RuntimeException{
    @Getter
    private final String chatId;

    public NoAvailableActionException(String message, String chatId){
        super(message);
        this.chatId = chatId;
    }

    public NoAvailableActionException(String message, String chatId, Exception cause){
        super(message,cause);
        this.chatId = chatId;
    }


}
