package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public interface MediaGroupHelper {

    Map<Integer, SendMediaGroup> convertMediaGroupMessages(Map<Integer, Message> mediaGroupMessages,
                                                           String chatId);
}
