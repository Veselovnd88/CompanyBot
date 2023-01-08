package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;

@Component
@Slf4j
public class ContactCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    @Autowired
    public ContactCallbackHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        if(data.equals("contact")){
            userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
            return SendMessage.builder().chatId(userId)
                    .text("Введите ФИО и контактные данные для обратной связи или контакт Телеграм")
                    .build();
        }
        return null;
    }
}
