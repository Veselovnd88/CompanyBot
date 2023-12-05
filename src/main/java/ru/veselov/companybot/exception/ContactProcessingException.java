package ru.veselov.companybot.exception;

public class ContactProcessingException extends RuntimeException {
    public ContactProcessingException(String message) {
        super(message);
    }
}
