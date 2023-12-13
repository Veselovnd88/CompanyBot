package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;

public interface InquiryMessageCreator {

    List<PartialBotApiMethod<?>> createBotMessagesToSend(InquiryModel inquiryModel, String chatId);

}
