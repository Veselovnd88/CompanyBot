package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
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

@Component
@Slf4j
public class ManageManagerByAdminCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final HandlerContext handlerContext;

    public ManageManagerByAdminCallbackHandler(UserDataCache userDataCache, @Lazy HandlerContext handlerContext) {
        this.userDataCache = userDataCache;
        this.handlerContext = handlerContext;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        switch (data){
            case "saveManager":
                userDataCache.setUserBotState(userId, BotState.AWAIT_MANAGER);
                return handlerContext.getHandler(BotState.AWAIT_MANAGER).processUpdate(update);

            case "deleteManager":
                userDataCache.setUserBotState(userId,BotState.DELETE_MANAGER);
                return handlerContext.getHandler(BotState.DELETE_MANAGER).processUpdate(update);
            case "exit":
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(
                                ManageKeyboardUtils.manageKeyboard()).build();

        }
        return null;
    }
}
