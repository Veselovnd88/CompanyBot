package ru.veselov.companybot.bot;

public class BotCommands {

    public static final String START = "/start";

    public static final String INQUIRY = "/inquiry";

    public static final String CALL = "/call";

    public static final String ABOUT = "/about";

    public static final String INFO = "/info";

    public static final String UPDATE_INFO = "/update_info";

    private BotCommands() {
        throw new AssertionError("This is private constructor for util class, no instances allowed");
    }
}
