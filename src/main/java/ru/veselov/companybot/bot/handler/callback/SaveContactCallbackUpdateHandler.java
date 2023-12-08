package ru.veselov.companybot.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;

public interface SaveContactCallbackUpdateHandler extends UpdateHandlerFromContext {

    @Override
    AnswerCallbackQuery processUpdate(Update update);

}
