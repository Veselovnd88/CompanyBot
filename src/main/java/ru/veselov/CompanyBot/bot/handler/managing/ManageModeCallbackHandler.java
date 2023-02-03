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
import ru.veselov.CompanyBot.exception.NoAvailableActionCallbackException;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class ManageModeCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ManageKeyboardUtils manageKeyboardUtils;
    @Autowired
    public ManageModeCallbackHandler(UserDataCache userDataCache, ManageKeyboardUtils manageKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionCallbackException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: нажата кнопка {}", userId, data);
        switch (data){
            case "managers":
                userDataCache.setUserBotState(userId, BotState.MANAGE_MANAGER);
                return SendMessage.builder().chatId(userId).text("Меню управления менеджерами")
                        .replyMarkup(manageKeyboardUtils.managersManageKeyboard()).build();
            case "divisions":
                userDataCache.setUserBotState(userId, BotState.MANAGE_DIVISION);
                return SendMessage.builder().chatId(userId).text("Меню управления отделами")
                        .replyMarkup(manageKeyboardUtils.divisionsManageKeyboard()).build();

            case "about":
                userDataCache.setUserBotState(userId, BotState.MANAGE_ABOUT);
                String currentInfo = MessageUtils.about.getText();
                return SendMessage.builder().chatId(userId)
                        .text(currentInfo+"\nПришлите новое описание")
                        .entities(MessageUtils.about.getEntities()).build();
            case "exit":
                userDataCache.setUserBotState(userId,BotState.READY);
                return SendMessage.builder().chatId(userId)
                        .text("Готов к работе, ожидаю следующей команды")
                        .build();
        }
        throw new NoAvailableActionCallbackException(MessageUtils.NOT_SUPPORTED_ACTION,
                update.getCallbackQuery().getId());
    }

}
