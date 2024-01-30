package ru.veselov.companybot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogMessageUtils {

    public static final String CUSTOMER_NOT_IN_DB_WARN = "Customer with {} was not in db, i will create new entity";

    public static final String DIVISION_NOT_IN_DB_WARN = "Division with id not found, choose base or will created new";

    public static final String NO_BASE_DIVISION_FOUND = "Base division is not found, created new";

}
