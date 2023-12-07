package ru.veselov.companybot.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandler;

public interface SaveContactCallbackUpdateHandler extends UpdateHandler {

    @Override
    AnswerCallbackQuery processUpdate(Update update);

}
