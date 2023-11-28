package ru.veselov.companybot.exception;

public class NoAvailableActionSendMessageException extends NoAvailableActionException{
    public NoAvailableActionSendMessageException(String message, String chatId) {
        super(message, chatId);
    }
    public NoAvailableActionSendMessageException(String message, String chatId, Exception cause){
        super(message,chatId,cause);
    }
}
