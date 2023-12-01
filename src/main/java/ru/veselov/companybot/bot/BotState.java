package ru.veselov.companybot.bot;

public enum BotState {
    BEGIN,

    READY,

    AWAIT_CONTACT,

    AWAIT_EMAIL,

    AWAIT_NAME,

    AWAIT_PHONE,

    AWAIT_SHARED,

    AWAIT_DIVISION_FOR_INQUIRY,

    AWAIT_MESSAGE,

    AWAIT_SAVING

}
