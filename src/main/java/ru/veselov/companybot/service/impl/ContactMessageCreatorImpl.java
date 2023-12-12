package ru.veselov.companybot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.service.ContactMessageCreator;
import ru.veselov.companybot.util.BotMessageStringUtils;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ContactMessageCreatorImpl implements ContactMessageCreator {

    @Override
    public List<BotApiMethod<?>> createBotMessagesToSend(ContactModel contact, Chat chat,
                                                         Boolean hasInquiry) {
        log.debug("Creating message with Contact to send");
        List<BotApiMethod<?>> messagesToSend = new LinkedList<>();
        SendMessage contactMessage = SendMessage.builder().chatId(chat.getId()).text(
                BotMessageStringUtils.createContactMessage(contact, hasInquiry).trim()).build();
        messagesToSend.add(contactMessage);
        log.debug("Send message with contact data added to list");
        if (contact.getContact() != null) {
            SendContact sendContact = new SendContact();
            sendContact.setChatId(chat.getId());
            sendContact.setLastName(contact.getContact().getLastName());
            sendContact.setFirstName(contact.getContact().getFirstName());
            sendContact.setVCard(contact.getContact().getVCard());
            sendContact.setPhoneNumber(contact.getContact().getPhoneNumber());
            messagesToSend.add(sendContact);
            log.debug("SendContact added to list");
        }
        return messagesToSend;
    }

}
