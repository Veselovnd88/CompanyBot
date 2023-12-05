package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.ContactMessageHandler;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.exception.WrongContactException;
import ru.veselov.companybot.model.ContactModel;

/**
 * Class for handling updates containing contact data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactMessageHandlerImpl implements ContactMessageHandler {

    private final UserDataCacheFacade userDataCacheFacade;

    private final ContactCache contactCache;

    private final KeyBoardUtils keyBoardUtils;

    private final ContactMessageProcessor contactMessageProcessor;

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCacheFacade.getUserBotState(userId);
        ContactModel contact = contactCache.getContact(userId);
        log.debug("[User id: {}] with [bot state {}] send contact data", userId, botState);
        if (update.getMessage().hasText()) {
            log.debug("Checking text message for contact");
            String text = update.getMessage().getText();
            try {
                switch (botState) {
                    case AWAIT_NAME:
                        EditMessageReplyMarkup addedNameMarkUp = contactMessageProcessor.processName(contact, text);
                        userDataCacheFacade.setUserBotState(userId, BotState.AWAIT_CONTACT);
                        return addedNameMarkUp;
                    case AWAIT_PHONE:
                        EditMessageReplyMarkup addedPhoneMarkUp = contactMessageProcessor.processPhone(contact, text);
                        userDataCacheFacade.setUserBotState(contact.getUserId(), BotState.AWAIT_CONTACT);
                        return addedPhoneMarkUp;
                    case AWAIT_EMAIL:
                        EditMessageReplyMarkup addedEmailMarkUp = contactMessageProcessor.processEmail(contact, text);
                        userDataCacheFacade.setUserBotState(contact.getUserId(), BotState.AWAIT_CONTACT);
                        return addedEmailMarkUp;
                    default:
                        throw new NoAvailableActionSendMessageException("Wrong bot state for this action",
                                userId.toString());
                }
            } catch (ContactProcessingException e) {
                log.warn("Error during processing contact: {}, [user id: {}]", e.getMessage(), userId);
                return SendMessage.builder().chatId(userId)
                        .text(e.getMessage()).replyMarkup(keyBoardUtils.contactKeyBoard())
                        .build();
            }
        }
        log.debug("Checking Contact Object for contact data");
        if (update.getMessage().hasContact()) {
            if (botState != BotState.AWAIT_SHARED) {
                throw new NoAvailableActionSendMessageException("Wrong bot state for this action", userId.toString());
            }
            EditMessageReplyMarkup editMessageReplyMarkup = contactMessageProcessor
                    .processSharedContact(contact, update.getMessage().getContact());
            userDataCacheFacade.setUserBotState(contact.getUserId(), BotState.AWAIT_CONTACT);
            return editMessageReplyMarkup;
        }
        log.warn("Wrong contact format for [user id: {}]", userId);
        throw new WrongContactException(MessageUtils.WRONG_CONTACT_FORMAT, userId.toString());
    }

}
