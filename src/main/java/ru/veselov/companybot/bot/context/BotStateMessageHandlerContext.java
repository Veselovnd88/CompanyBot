package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.BotState;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class for convenient access to handler by {@link BotState}
 */
@Component
@Slf4j
public class BotStateMessageHandlerContext {

    private final Map<BotState, UpdateHandlerFromContext> botStateUpdateHandlerMap = new EnumMap<>(BotState.class);

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
