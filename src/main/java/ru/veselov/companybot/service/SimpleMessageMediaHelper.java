package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public interface SimpleMessageMediaHelper {

    Map<Integer, PartialBotApiMethod<?>> convertSendMediaMessage(Map<Integer, Message> messages, String chatId);

}
