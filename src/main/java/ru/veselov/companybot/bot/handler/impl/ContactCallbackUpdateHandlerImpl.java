package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.exception.handler.BotExceptionToMessage;
import ru.veselov.companybot.service.CustomerDataHandler;

/**
 * Class for handling updates containing CallBacks for contact managing;
 *
 * @see UserDataCacheFacade
 * @see CustomerDataHandler
 * @see KeyBoardUtils
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactCallbackUpdateHandlerImpl implements ContactCallbackUpdateHandler {

    private final CustomerDataHandler customerDataHandler;

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
        switch (data) {
            case CallBackButtonUtils.EMAIL:
                userDataCache.setUserBotState(userId, BotState.AWAIT_EMAIL);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.EMAIL);
            case CallBackButtonUtils.PHONE:
                userDataCache.setUserBotState(userId, BotState.AWAIT_PHONE);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.PHONE);
            case CallBackButtonUtils.SHARED:
                userDataCache.setUserBotState(userId, BotState.AWAIT_SHARED);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.SHARED);
            case CallBackButtonUtils.NAME:
                userDataCache.setUserBotState(userId, BotState.AWAIT_NAME);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.NAME);
            case CallBackButtonUtils.CONTACT, CallBackButtonUtils.REPEAT://went from InquiryMessageHandler
                //reset all and gives new form for filling contact
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                userDataCache.createContact(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .replyMarkup(keyBoardUtils.contactKeyBoard())
                        .build();
            case CallBackButtonUtils.SAVE:
                customerDataHandler.handle(userId);
                return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                        .text(MessageUtils.SAVED).showAlert(true)
                        .build();
            default:
                throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, update.getCallbackQuery().getId());
        }
    }

}
