package ru.veselov.companybot.bot.handler.message;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;

public interface ContactMessageUpdateHandler extends UpdateHandlerFromContext {

    @Override
    EditMessageReplyMarkup processUpdate(Update update);

}
