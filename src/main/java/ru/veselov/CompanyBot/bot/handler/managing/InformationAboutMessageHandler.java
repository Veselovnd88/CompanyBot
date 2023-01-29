package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.CompanyInfoService;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class InformationAboutMessageHandler implements UpdateHandler {
    private final CompanyInfoService companyInfoService;
    private final UserDataCache userDataCache;
    @Autowired
    public InformationAboutMessageHandler(CompanyInfoService companyInfoService, UserDataCache userDataCache) {
        this.companyInfoService = companyInfoService;
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        if(!update.getMessage().hasText()){
            return SendMessage.builder().chatId(userId)
                    .text("Сообщение не содержит текст").build();
        }
        String text = update.getMessage().getText();
        if(text.length()>900){
            return SendMessage.builder().chatId(userId).text("Описание не должно превышать 900 символов").build();
        }
        MessageUtils.about=update.getMessage();
        companyInfoService.save(update.getMessage());
        userDataCache.setUserBotState(userId, BotState.READY);
        return SendMessage.builder().chatId(userId).text("Установлено новое описание").build();
    }
}
