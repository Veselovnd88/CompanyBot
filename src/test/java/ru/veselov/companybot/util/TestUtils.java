package ru.veselov.companybot.util;

import net.datafaker.Faker;
import ru.veselov.companybot.model.DivisionModel;

import java.util.UUID;

public class TestUtils {

    public static Faker faker = new Faker();

    public static Long BOT_ID = 1L;

    public static final Long USER_ID = 2L;

    public static final Long ADMIN_ID = 3L;

    public static final String ADMIN_NAME = faker.elderScrolls().dragon();

    public static final String ADMIN_FIRST_NAME = faker.elderScrolls().firstName();

    public static final String ADMIN_LAST_NAME = faker.elderScrolls().lastName();

    public static final String USER_NAME = faker.elderScrolls().creature();

    public static final String USER_FIRST_NAME = faker.elderScrolls().firstName();

    public static final String USER_LAST_NAME = faker.elderScrolls().lastName();


    public static final String DIVISION_NAME = "NAME";

    public static DivisionModel getDivision() {
        return DivisionModel.builder()
                .divisionId(UUID.randomUUID())
                .name(DIVISION_NAME)
                .build();
    }

}
