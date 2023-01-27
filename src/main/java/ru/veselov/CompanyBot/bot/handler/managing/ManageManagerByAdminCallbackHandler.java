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
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.BotAnswerUtil;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;

@Component
@Slf4j
public class ManageManagerByAdminCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final BotAnswerUtil botAnswerUtil;
    private final ManageKeyboardUtils manageKeyboardUtils;
    private final ManagerService managerService;
    @Autowired
    public ManageManagerByAdminCallbackHandler(UserDataCache userDataCache, BotAnswerUtil botAnswerUtil, ManageKeyboardUtils manageKeyboardUtils, ManagerService managerService) {
        this.userDataCache = userDataCache;
        this.botAnswerUtil = botAnswerUtil;
        this.manageKeyboardUtils = manageKeyboardUtils;
        this.managerService = managerService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        if(data.equalsIgnoreCase("saveManager")){
            userDataCache.setUserBotState(userId, BotState.AWAIT_MANAGER);
            return botAnswerUtil.getAnswerAwaitManager(userId);}
        else if(data.equalsIgnoreCase("deleteManager")){
            userDataCache.setUserBotState(userId,BotState.DELETE_MANAGER);
            return botAnswerUtil.getAnswerAwaitManager(userId);}
        else if(data.equalsIgnoreCase("exit")||data.equalsIgnoreCase("show")){
            userDataCache.setUserBotState(userId,BotState.MANAGE);
            if(data.equalsIgnoreCase("show")){
                //todo managerService findAll
                return SendMessage.builder().chatId(userId)
                        .replyMarkup(manageKeyboardUtils.manageKeyboard())
                        .text("Список менеджеров").build();
            }
            return SendMessage.builder().chatId(userId)
                    .replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .text("Режим управления").build();}
        else{
            return SendMessage.builder().chatId(userId)
                    .text("Не корректная кнопка").build();
        }
    }
}
