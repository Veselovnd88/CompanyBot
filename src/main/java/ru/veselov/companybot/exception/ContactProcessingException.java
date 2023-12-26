package ru.veselov.companybot.exception;

/**
 * Throws during problems with processing of contact data
 */
public class ContactProcessingException extends ProcessUpdateException {

    public ContactProcessingException(String message, String chatId) {
        super(message, chatId);
    }

}
