package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.UpdateHandler;

@Component
@Slf4j
public class AddManagerByAdminMessageHandler implements UpdateHandler {

    @Override
    public BotApiMethod<?> processUpdate(Update update) {

        return null;
    }
}
