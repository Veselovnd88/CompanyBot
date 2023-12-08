package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.BotState;

import java.util.EnumMap;
import java.util.Map;

@Component
@Slf4j
public class BotStateHandlerContext {

    private final Map<BotState, UpdateHandler> botStateUpdateHandlerMap = new EnumMap<>(BotState.class);

    public void add(BotState botState, UpdateHandler updateHandler) {
        log.info("[Handler: {}] added to context for [bot state: {}]",
                updateHandler.getClass().getSimpleName(), botState);
        botStateUpdateHandlerMap.put(botState, updateHandler);
    }
}
