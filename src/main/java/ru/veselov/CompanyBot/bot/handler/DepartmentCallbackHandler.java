package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.UpdateHandler;

@Component
@Slf4j
public class DepartmentCallbackHandler implements UpdateHandler {
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        //TODO в кеше создается мапа с inquiry, и к этому inquiry добавляется енам Department
        switch (data){
            case "leuze":
                break;
            case "lpkf":
                return null;
            case "pressure":
                return null;
            case "common":
                return null;
        }
        return null;
    }
}
