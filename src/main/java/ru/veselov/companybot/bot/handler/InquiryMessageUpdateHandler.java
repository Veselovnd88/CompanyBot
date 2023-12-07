package ru.veselov.companybot.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.context.UpdateHandler;

public interface InquiryMessageUpdateHandler extends UpdateHandler {

    SendMessage processUpdate(Update update);

}
