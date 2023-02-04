package ru.veselov.CompanyBot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.exception.NoSuchDivisionException;

public interface Sender{
    void send(CompanyBot bot, Chat chat) throws TelegramApiException, NoSuchDivisionException;
}
