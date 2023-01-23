package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.HandlerContext;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class ManageManagerByAdminCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final HandlerContext handlerContext;
    private final ManageKeyboardUtils manageKeyboardUtils;
    @Autowired
    public ManageManagerByAdminCallbackHandler(UserDataCache userDataCache, @Lazy HandlerContext handlerContext, ManageKeyboardUtils manageKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.handlerContext = handlerContext;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        switch (data){
            case "saveManager":
                userDataCache.setUserBotState(userId, BotState.AWAIT_MANAGER);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.AWAIT_MANAGER).build();//fixme

            case "deleteManager":
                userDataCache.setUserBotState(userId,BotState.DELETE_MANAGER);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.AWAIT_MANAGER).build();//fixme
            case "exit":
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .replyMarkup(manageKeyboardUtils.manageKeyboard())
                        .text("Режим управления").build();

        }
        return null;
    }
}
