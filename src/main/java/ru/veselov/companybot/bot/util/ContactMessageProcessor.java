package ru.veselov.companybot.bot.util;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import ru.veselov.companybot.model.ContactModel;

public interface ContactMessageProcessor {

    EditMessageReplyMarkup processName(ContactModel contact, String name);

    EditMessageReplyMarkup processPhone(ContactModel contact, String phone);

    EditMessageReplyMarkup processEmail(ContactModel contact, String email);

}
