package ru.veselov.companybot.bot.handler;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface SaveContactCallbackUpdateHandler {

    AnswerCallbackQuery processUpdate(Update update);

}
