package ru.veselov.companybot.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ChannelConnectUpdateHandler {

    SendMessage processUpdate(Update update);

}
