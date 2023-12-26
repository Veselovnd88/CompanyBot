package ru.veselov.companybot.bot.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateMessageHandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.callback.CallbackQueryUpdateHandler;
import ru.veselov.companybot.bot.util.BotUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.util.MessageUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryUpdateHandlerImpl implements CallbackQueryUpdateHandler {

    private final CallbackQueryHandlerContext callbackQueryHandlerContext;

    private final UserDataCacheFacade userDataCache;

    /**
     * Receive update and choose suitable handler for processing
     *
     * @param update {@link Update} update from Telegram
     *               <p>
     *               1) Check {@link CallbackQueryHandlerContext} by callback data
     *               <p>
     *               2) Check {@link BotStateMessageHandlerContext} by current bot state
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

        updateHandler = callbackQueryHandlerContext.getFromDataContext(callbackData);
        if (updateHandler != null) {
            BotUtils.validateUpdateHandlerStates(updateHandler, botState, chatId);
            log.debug("[{}] retrieved from call back data context", updateHandler.getClass().getSimpleName());
            return updateHandler.processUpdate(update);
        } else {
            updateHandler = callbackQueryHandlerContext.getFromBotStateContext(botState);
            if (updateHandler != null) {
                BotUtils.validateUpdateHandlerStates(updateHandler, botState, chatId);
                log.debug("[{}] retrieved from bot state context", updateHandler.getClass().getSimpleName());
                return updateHandler.processUpdate(update);
            } else {
                log.warn("No handler for this data or bot state");
                throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
            }
        }
    }

}