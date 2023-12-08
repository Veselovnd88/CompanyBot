package ru.veselov.companybot.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;

public interface ContactCallbackUpdateHandler extends UpdateHandlerFromContext {
    @Override
    EditMessageReplyMarkup processUpdate(Update update);

}
