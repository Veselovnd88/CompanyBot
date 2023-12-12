package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.model.ContactModel;

import java.util.List;

public interface ContactMessageCreator {

    List<BotApiMethod<?>> createBotMessagesToSend(ContactModel contact, Chat chat, Boolean hasInquiry);

}
