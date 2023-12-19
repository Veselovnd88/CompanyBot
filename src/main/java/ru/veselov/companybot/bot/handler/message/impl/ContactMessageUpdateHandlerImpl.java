package ru.veselov.companybot.bot.handler.message.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.handler.message.ContactMessageUpdateHandler;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.exception.WrongContactException;
import ru.veselov.companybot.exception.util.ExceptionMessageUtils;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Set;

/**
 * Class for handling updates containing contact data for setting up {@link ContactModel};
 *
 * @see UserDataCacheFacade
 * @see ContactCache
 * @see ContactMessageProcessor
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactMessageUpdateHandlerImpl implements ContactMessageUpdateHandler {

    private final UserDataCacheFacade userDataCacheFacade;

    private final ContactCache contactCache;

    private final ContactMessageProcessor contactMessageProcessor;

    private final BotStateHandlerContext context;

    @Override
    @PostConstruct
    public void registerInContext() {
        for (var state : getAvailableStates()) {
            context.add(state, this);
        }
    }

    /**
     * Processing update from Telegram, processing is only available for {@link BotState}:
     * <p>
     * {@link BotState#AWAIT_NAME}, {@link BotState#AWAIT_PHONE},  {@link BotState#AWAIT_EMAIL},
     * {@link BotState#AWAIT_SHARED}
     * </p>
     * after successful processing set up to {@link BotState#AWAIT_CONTACT}
     *
     * @return {@link EditMessageReplyMarkup} TG object for changing current message
     * @throws WrongBotStateException     if entering with wrong BotState
     * @throws ContactProcessingException if error occurred during processing contact data
     * @throws WrongContactException      if update's message doesn't contain text or shared contact
     */
    @Override
    public EditMessageReplyMarkup processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCacheFacade.getUserBotState(userId);
        ContactModel contact = contactCache.getContact(userId);
        log.debug("[User id: {}] with [bot state {}] send contact data", userId, botState);
        if (update.getMessage().hasText()) {
            log.debug("Checking text message for contact");
            String text = update.getMessage().getText();
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
                    log.warn(ExceptionMessageUtils.WRONG_STATE_FOR_THIS_ACTION);
                    throw new WrongBotStateException(MessageUtils.ANOTHER_ACTION, userId.toString());
            }
        }
        log.debug("Checking Contact Object for contact data");
        if (update.getMessage().hasContact()) {
            if (botState != BotState.AWAIT_SHARED) {
                log.warn(ExceptionMessageUtils.WRONG_STATE_FOR_THIS_ACTION);
                throw new WrongBotStateException(MessageUtils.ANOTHER_ACTION, userId.toString());
            }
            EditMessageReplyMarkup editMessageReplyMarkup = contactMessageProcessor
                    .processSharedContact(contact, update.getMessage().getContact());
            userDataCacheFacade.setUserBotState(contact.getUserId(), BotState.AWAIT_CONTACT);
            return editMessageReplyMarkup;
        }
        log.warn("Wrong contact format for [user id: {}]", userId);
        throw new WrongContactException(MessageUtils.WRONG_CONTACT_FORMAT, userId.toString());
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(
                BotState.AWAIT_CONTACT, BotState.AWAIT_PHONE, BotState.AWAIT_NAME,
                BotState.AWAIT_EMAIL,
                BotState.AWAIT_SHARED
        );
    }

}
