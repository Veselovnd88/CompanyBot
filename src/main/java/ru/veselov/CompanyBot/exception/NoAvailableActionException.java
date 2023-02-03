package ru.veselov.CompanyBot.exception;

import lombok.Getter;

public class NoAvailableActionException extends Exception{
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
