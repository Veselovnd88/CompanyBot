package ru.veselov.CompanyBot.exception;

public class NoAvailableActionCallbackException extends NoAvailableActionException{
    public NoAvailableActionCallbackException(String message, String chatId) {
        super(message, chatId);
    }
    public NoAvailableActionCallbackException(String message, String chatId, Exception cause){
        super(message, chatId, cause);
    }
}
