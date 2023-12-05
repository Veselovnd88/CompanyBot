package ru.veselov.companybot.bot.util.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.model.ContactModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactMessageProcessorImpl implements ContactMessageProcessor {

    private final KeyBoardUtils keyBoardUtils;

    private final EmailValidator emailValidator;

    @Override
    public EditMessageReplyMarkup processName(ContactModel contact, String name) {
        log.debug("Processing name of contact");
        Long userId = contact.getUserId();
        if (name.length() > 250) {
            log.info("Try to enter name more >250 symbols: [user id {}]", userId);
            throw new ContactProcessingException(MessageUtils.NAME_TOO_LONG);
        }
        String[] s = name.split(" ");
        if (s.length == 0 || name.isEmpty()) {
            log.info("Entered empty string by [user id {}]", userId);
            throw new ContactProcessingException(MessageUtils.WRONG_CONTACT_FORMAT);
        }
        contact.setLastName(s[0].trim());
        if (s.length > 1) {
            contact.setFirstName(s[1].trim());
        }
        if (s.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < s.length; i++) {
                sb.append(s[i]).append(" ");
            }
            contact.setSecondName(sb.toString().trim());
        }
        log.info("Name {} added to contact for [user id: {}]", name, userId);
        return keyBoardUtils.editMessageSavedField(userId, "name");
    }

    @Override
    public EditMessageReplyMarkup processPhone(ContactModel contact, String phone) {
        log.debug("Processing phone of contact");
        Pattern pattern = Pattern.compile("^[+]?[-. 0-9{}]{11,18}$");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            contact.setPhone(phone);
            log.info("Phone number {} was set up for [user id: {}]", contact.getUserId(), phone);
            return keyBoardUtils.editMessageSavedField(contact.getUserId(), "phone");
        } else {
            log.warn("Incorrect phone number for [user id: {}]", contact.getUserId());
            throw new ContactProcessingException(MessageUtils.WRONG_PHONE);
        }
    }

    @Override
    public EditMessageReplyMarkup processEmail(ContactModel contact, String email) {
        log.debug("Processing email of contact");
        Long userId = contact.getUserId();
        if (emailValidator.isValid(email, null)) {
            contact.setEmail(email);
            log.info("Email: {} added for [user id: {}]", email, userId);
            return keyBoardUtils.editMessageSavedField(userId, "email");
        } else {
            log.warn("Not correct email format for [user id: {}]", userId);
            throw new ContactProcessingException(MessageUtils.WRONG_EMAIL);
        }
    }

}
