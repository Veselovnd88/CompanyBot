package ru.veselov.companybot.exception;

import jakarta.persistence.EntityNotFoundException;

public class InquiryNotFoundException extends EntityNotFoundException {

    public InquiryNotFoundException(String message) {
        super(message);
    }

}
