package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CallbackQueryDataHandlerContext {

    private final Map<String, UpdateHandlerFromContext> callbackHandlerContext = new HashMap<>();

    public void add(String data, UpdateHandlerFromContext updateHandler) {
        log.info("[Handler: {}] added to context for [callback data: {}]", updateHandler.getClass().getSimpleName(), data);
        callbackHandlerContext.put(data, updateHandler);
    }

    public UpdateHandlerFromContext getHandler(String data) {
        return callbackHandlerContext.get(data);
    }

}
