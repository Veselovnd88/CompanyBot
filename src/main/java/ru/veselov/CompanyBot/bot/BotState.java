package ru.veselov.CompanyBot.bot;

public enum BotState {
    BEGIN,
    READY,
    AWAIT_CONTACT,
    AWAIT_EMAIL,
    AWAIT_NAME,
    AWAIT_PHONE,
    AWAIT_SHARED,
    AWAIT_DEPARTMENT,
    AWAIT_MESSAGE,
    AWAIT_SAVING,
    MANAGE,
    MANAGE_MANAGER,
    MANAGE_DIVISION,
    MANAGE_ABOUT,
    AWAIT_MANAGER,
    ASSIGN_DIV;
}
