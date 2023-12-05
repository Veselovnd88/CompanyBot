package ru.veselov.companybot.bot.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.UpdateHandler;

public interface ContactMessageHandler extends UpdateHandler {

    @Override
    BotApiMethod<?> processUpdate(Update update);

}
