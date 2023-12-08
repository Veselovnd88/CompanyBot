package ru.veselov.companybot.bot.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CallbackQueryDataHandlerContext {

    private final Map<String, UpdateHandler> callbackHandlerContext = new HashMap<>();

    public void add(String data, UpdateHandler updateHandler) {
        log.info("[Handler: {}] added to context for [callback data: {}]", updateHandler.getClass().getSimpleName(), data);
        callbackHandlerContext.put(data, updateHandler);
    }

    public UpdateHandler getHandler(String data) {
        return callbackHandlerContext.get(data);
    }

}
