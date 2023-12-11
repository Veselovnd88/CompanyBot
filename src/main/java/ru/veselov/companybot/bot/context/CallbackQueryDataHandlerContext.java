package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for convenient access to handler by data from {@link CallbackQuery}
 */
@Component
@Slf4j
public class CallbackQueryDataHandlerContext {

    private final Map<String, UpdateHandlerFromContext> callbackHandlerContext = new HashMap<>();

    /**
     * Add responsible handler to context
     *
     * @param data          {@link String} data from {@link CallbackQuery}
     * @param updateHandler {@link UpdateHandlerFromContext} handler that we want to add
     */
    public void add(String data, UpdateHandlerFromContext updateHandler) {
        log.info("[Handler: {}] added to context for [callback data: {}]", updateHandler.getClass().getSimpleName(), data);
        callbackHandlerContext.put(data, updateHandler);
    }

    /**
     * @param data {@link String} data from {@link CallbackQuery}
     * @return {@link UpdateHandlerFromContext} handler responsible for callback data
     */
    public UpdateHandlerFromContext getHandler(String data) {
        return callbackHandlerContext.get(data);
    }

}
