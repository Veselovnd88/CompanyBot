package ru.veselov.companybot.bot.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.callback.CallbackQueryUpdateHandler;
import ru.veselov.companybot.bot.util.BotStateUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryUpdateHandlerImpl implements CallbackQueryUpdateHandler {

    private final BotStateHandlerContext botStateHandlerContext;

    private final CallbackQueryDataHandlerContext callbackQueryDataHandlerContext;

    private final UserDataCacheFacade userDataCache;

    /**
     * Receive update and choose suitable handler for processing
     * @param update {@link Update} update from Telegram
     * <p>
     * 1) Check {@link CallbackQueryDataHandlerContext} by callback data
     * <p>
     * 2) Check {@link BotStateHandlerContext} by current bot state
     *
     * @throws UnexpectedActionException if suitable handler not found in both contexts
     */
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        //check for handler in data context
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        BotState botState = userDataCache.getUserBotState(callbackQuery.getFrom().getId());
        String chatId = callbackQuery.getId();

        UpdateHandlerFromContext updateHandler;

        updateHandler = callbackQueryDataHandlerContext.getHandler(callbackData);
        if (updateHandler != null) {
            BotStateUtils.validateUpdateHandlerStates(updateHandler, botState, chatId);
            log.debug("[{}] retrieved from call back data context", updateHandler.getClass().getSimpleName());
            return updateHandler.processUpdate(update);
        } else {
            updateHandler = botStateHandlerContext.getHandler(botState);
            if (updateHandler != null) {
                BotStateUtils.validateUpdateHandlerStates(updateHandler, botState, chatId);
                log.debug("[{}] retrieved from botstate data context", updateHandler.getClass().getSimpleName());
                return updateHandler.processUpdate(update);
            } else {
                log.warn("No handler for this data or botstate");
                throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
            }
        }
    }

}