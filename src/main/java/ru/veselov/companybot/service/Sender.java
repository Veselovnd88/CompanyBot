package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;

public interface Sender {
    void send(CompanyBot bot, Chat chat);
}
