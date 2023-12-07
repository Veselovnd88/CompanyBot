package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.exception.handler.BotExceptionToMessage;

/**
 * Class for handling updates containing CallBacks for contact managing;
 *
 * @see UserDataCacheFacade
 * @see KeyBoardUtils
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactCallbackUpdateHandlerImpl implements ContactCallbackUpdateHandler {

    private final UserDataCacheFacade userDataCache;

    private final KeyBoardUtils keyBoardUtils;

    @Value("${bot.adminId}")
    private String adminId;

    /**
     * Handle process update with CallBackQuery data
     * <p>
     * Chose according bot state depending on data and return keyboard or answer
     * </p>
     *
     * @return {@link BotApiMethod} EditMessageReplyMarkup or AnswerCallBackQuery
     * @throws ContactProcessingException if contact was not correctly filled
     * @throws UnexpectedActionException  if no data from keyboard was passed
     */
    @BotExceptionToMessage
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.debug("Process [callback: {}] from contact keyboard [user id: {}]", data, userId);
        // case CallBackButtonUtils.CONTACT, CallBackButtonUtils.REPEAT://went from InquiryMessageHandler
        //reset all and gives new form for filling contact
        userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
        userDataCache.createContact(userId);
        return SendMessage.builder().chatId(userId)
                .text(MessageUtils.INPUT_CONTACT)
                .replyMarkup(keyBoardUtils.contactKeyBoard())
                .build();
    }
}

