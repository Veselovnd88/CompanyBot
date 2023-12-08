package ru.veselov.companybot.bot.handler.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;

public interface InquiryMessageUpdateHandler extends UpdateHandlerFromContext {

    SendMessage processUpdate(Update update);

}
