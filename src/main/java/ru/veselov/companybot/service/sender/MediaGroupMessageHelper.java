package ru.veselov.companybot.service.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public interface MediaGroupMessageHelper {

    Map<Integer, SendMediaGroup> convertMediaGroupMessages(Map<Integer, Message> mediaGroupMessages,
                                                           String chatId);
}
