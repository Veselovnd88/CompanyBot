package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class InformationAboutMessageHandler implements UpdateHandler {
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        String text = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        if(text.length()>900){
            return SendMessage.builder().chatId(userId).text("Описание не должно превышать 900 символов").build()
        }
        MessageUtils.about=update.getMessage();
        //TODO сохранять описание в БД
        return SendMessage.builder().chatId(userId).text("Установлено новое описание").build();
    }
}
