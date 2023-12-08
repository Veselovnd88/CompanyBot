package ru.veselov.companybot.bot.context;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;

import java.util.Set;

public interface UpdateHandlerFromContext {
    BotApiMethod<?> processUpdate(Update update);

    Set<BotState> getAvailableStates();

    void registerInContext();

}
