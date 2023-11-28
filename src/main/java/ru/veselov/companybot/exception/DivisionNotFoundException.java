package ru.veselov.companybot.exception;

import jakarta.persistence.EntityNotFoundException;

public class DivisionNotFoundException  extends EntityNotFoundException {

    public DivisionNotFoundException(String message) {
        super(message);
    }
}
