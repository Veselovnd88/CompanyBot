package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.veselov.companybot.bot.BotState;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for convenient access to handler by data from {@link CallbackQuery}
 */
@Component
@Slf4j
public class CallbackQueryHandlerContext {

    private final Map<String, UpdateHandlerFromContext> callbackHandlerContext = new HashMap<>();

    private final Map<BotState, UpdateHandlerFromContext> botStateUpdateHandlerMap = new EnumMap<>(BotState.class);

    /**
     * Add responsible handler to context
     *
     * @param data          {@link String} data from {@link CallbackQuery}
     * @param updateHandler {@link UpdateHandlerFromContext} handler that we want to add
     */
    public void addToDataContext(String data, UpdateHandlerFromContext updateHandler) {
        log.info("[Handler: {}] added to context for [callback data: {}]", updateHandler.getClass().getSimpleName(), data);
        callbackHandlerContext.put(data, updateHandler);
    }

    /**
     * @param data {@link String} data from {@link CallbackQuery}
     * @return {@link UpdateHandlerFromContext} handler responsible for callback data
     */
    public UpdateHandlerFromContext getFromDataContext(String data) {
        return callbackHandlerContext.get(data);
    }

    /**
     * Add responsible handler to context
     *
     * @param botState      {@link BotState} bot state for calling handler
     * @param updateHandler {@link UpdateHandlerFromContext} handler that we want to add
     */
    public void addToBotStateContext(BotState botState, UpdateHandlerFromContext updateHandler) {
        log.info("[Handler: {}] added to context for [bot state: {}]",
                updateHandler.getClass().getSimpleName(), botState);
        botStateUpdateHandlerMap.put(botState, updateHandler);
    }

    /**
     * @param botState {@link BotState} bot state
     * @return {@link UpdateHandlerFromContext} handler responsible botState
     */
    public UpdateHandlerFromContext getFromBotStateContext(BotState botState) {
        return botStateUpdateHandlerMap.get(botState);
    }

}
