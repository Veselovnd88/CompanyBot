package ru.veselov.companybot.exception.util;

public class ExceptionMessageUtils {

    public static final String WRONG_STATE_FOR_THIS_ACTION = "Wrong bot state for this action: {}";

    public static final String SMTH_WENT_WRONG = "Something went wrong during send message to {}, msg: {}";

    public static final String HANDLED_EXCEPTION_WITH_MESSAGE = "Handled {} exception with message {}";

    public static final String NO_KEYBOARD_MESSAGE = "No saved keyboard for this [user id: %s], return new";

    public static final String DIVISION_ALREADY_EXISTS = "Division with name %s already exists";

    public static final String DIVISION_NOT_FOUND = "Division with [id: %s] not found";

    public static final String INQUIRY_NOT_FOUND = "Inquiry with [id: %s] not found";

    private ExceptionMessageUtils() {
        throw new AssertionError("No instances for util class");
    }
}
