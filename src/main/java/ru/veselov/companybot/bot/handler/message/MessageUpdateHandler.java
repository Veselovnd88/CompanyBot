package ru.veselov.companybot.bot.handler.message;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageUpdateHandler {

    BotApiMethod<?> processUpdate(Update update);

}
