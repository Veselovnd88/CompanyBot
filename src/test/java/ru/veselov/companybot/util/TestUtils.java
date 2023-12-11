package ru.veselov.companybot.util;

import ru.veselov.companybot.model.DivisionModel;

import java.util.UUID;

public class TestUtils {

    public static Long BOT_ID = 1L;

    public static final Long USER_ID = 2L;

    public static final String DIVISION_NAME = "NAME";

    public static DivisionModel getDivision() {
        return DivisionModel.builder()
                .divisionId(UUID.randomUUID())
                .name(DIVISION_NAME)
                .build();
    }

}
