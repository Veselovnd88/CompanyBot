package ru.veselov.companybot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.exception.NoAvailableActionException;

public interface UpdateHandler {
    BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionException;

}
