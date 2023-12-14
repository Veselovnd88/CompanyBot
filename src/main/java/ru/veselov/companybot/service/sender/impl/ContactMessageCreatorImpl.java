package ru.veselov.companybot.service.sender.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.service.sender.ContactMessageCreator;
import ru.veselov.companybot.util.MessageUtils;

import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class ContactMessageCreatorImpl implements ContactMessageCreator {

    @Override
    public List<BotApiMethod<?>> createBotMessagesToSend(ContactModel contact, String chatId, Boolean hasInquiry) {
        log.debug("Creating message with Contact to send");
        List<BotApiMethod<?>> messagesToSend = new LinkedList<>();
        SendMessage contactMessage = SendMessage.builder().chatId(chatId)
                .text(MessageUtils.createContactMessage(contact, hasInquiry)).build();
        messagesToSend.add(contactMessage);
        log.debug("Send message with contact data added to list");
        if (contact.getContact() != null) {
            SendContact sendContact = new SendContact();
            sendContact.setChatId(chatId);
            sendContact.setLastName(contact.getContact().getLastName());
            sendContact.setFirstName(contact.getContact().getFirstName());
            sendContact.setVCard(contact.getContact().getVCard());
            sendContact.setPhoneNumber(contact.getContact().getPhoneNumber());
            messagesToSend.add(sendContact);
            log.debug("SendContact added to list");
        }
        log.debug("List with {} messages for sending is ready", messagesToSend.size());
        return messagesToSend;
    }

}
