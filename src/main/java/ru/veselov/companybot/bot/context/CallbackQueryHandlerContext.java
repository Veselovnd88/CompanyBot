package ru.veselov.companybot.bot.context;

import ru.veselov.companybot.bot.UpdateHandler;

import java.util.HashMap;
import java.util.Map;

public class CallbackQueryHandlerContext {

    private final Map<String, UpdateHandler> messageHandlerContext = new HashMap<>();
}
