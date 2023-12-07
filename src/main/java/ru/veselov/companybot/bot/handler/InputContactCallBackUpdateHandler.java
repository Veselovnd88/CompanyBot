package ru.veselov.companybot.bot.handler;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.UpdateHandler;

public interface InputContactCallBackUpdateHandler extends UpdateHandler {

    @Override
    EditMessageReplyMarkup processUpdate(Update update);

}
