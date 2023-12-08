package ru.veselov.companybot.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;

public interface DivisionCallbackUpdateHandler extends UpdateHandlerFromContext {
    @Override
    SendMessage processUpdate(Update update);

}
