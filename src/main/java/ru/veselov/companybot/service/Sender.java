package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.exception.NoSuchDivisionException;

public interface Sender{
    void send(CompanyBot bot, Chat chat) throws TelegramApiException, NoSuchDivisionException;
}
