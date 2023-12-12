package ru.veselov.companybot.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackQueryUpdateHandler {

    BotApiMethod<?> processUpdate(Update update);

}
