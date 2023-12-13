package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.veselov.companybot.model.ContactModel;

import java.util.List;

public interface ContactMessageCreator {

    List<BotApiMethod<?>> createBotMessagesToSend(ContactModel contact, String chatId, Boolean hasInquiry);

}
