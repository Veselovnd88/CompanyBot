package ru.veselov.companybot.exception;

public class ContactProcessingException extends ProcessUpdateException {

    public ContactProcessingException(String message, String chatId) {
        super(message, chatId);
    }

}
