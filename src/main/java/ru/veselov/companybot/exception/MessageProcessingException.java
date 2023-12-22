package ru.veselov.companybot.exception;

public class MessageProcessingException extends ProcessUpdateException {
    public MessageProcessingException(String message, String chatId) {
        super(message, chatId);
    }
}
