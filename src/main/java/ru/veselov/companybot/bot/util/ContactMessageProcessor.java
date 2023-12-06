package ru.veselov.companybot.bot.util;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Contact;
import ru.veselov.companybot.model.ContactModel;

/**
 * Interface for setting up passed contact data to {@link ContactModel}
 *
 * @author Veselov Nikolay
 */
public interface ContactMessageProcessor {

    EditMessageReplyMarkup processName(ContactModel contact, String name);

    EditMessageReplyMarkup processPhone(ContactModel contact, String phone);

    EditMessageReplyMarkup processEmail(ContactModel contact, String email);

    EditMessageReplyMarkup processSharedContact(ContactModel contact, Contact shared);

}
