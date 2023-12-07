package ru.veselov.companybot.bot.context;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ContextUpdateHandler {

    BotApiMethod<?> processUpdate(Update update);

    void register(String data, CallbackQueryHandlerContext context);

}
