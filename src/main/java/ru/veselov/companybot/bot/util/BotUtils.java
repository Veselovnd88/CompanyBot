package ru.veselov.companybot.bot.util;

import lombok.extern.slf4j.Slf4j;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.exception.UnexpectedCallbackException;
import ru.veselov.companybot.util.MessageUtils;

@Slf4j
public class BotUtils {

    public static final String CUSTOM_EMOJI = "custom_emoji";

    public static final String BASE_DIVISION = "COMMON";

    /**
     * Check if current bot state is available for calling handler from context
     *
     * @param updateHandler {@link UpdateHandlerFromContext} handler found in context
     * @param botState      {@link  BotState} current bot state
     * @param chatId        {@link String} chat id for placing in Exception
     * @throws UnexpectedCallbackException if current bot state not available for handler
     */
    public static void validateUpdateHandlerStates(UpdateHandlerFromContext updateHandler, BotState botState, String chatId) {
        if (!updateHandler.getAvailableStates().contains(botState)) {
            log.warn("[{}] not available for call {}", botState, updateHandler.getClass().getSimpleName());
            throw new UnexpectedCallbackException(MessageUtils.ANOTHER_ACTION, chatId);
        }
    }

    private BotUtils() {
    }
}
