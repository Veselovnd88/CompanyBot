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
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

@Component
@Slf4j
public class ManagerMenuCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ManageKeyboardUtils manageKeyboardUtils;
    private final ManagerService managerService;
    @Autowired
    public ManagerMenuCallbackHandler(UserDataCache userDataCache, ManageKeyboardUtils manageKeyboardUtils, ManagerService managerService) {
        this.userDataCache = userDataCache;
        this.manageKeyboardUtils = manageKeyboardUtils;
        this.managerService = managerService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        if(data.equalsIgnoreCase("saveManager")){
            userDataCache.setUserBotState(userId, BotState.AWAIT_MANAGER);
            return getAnswerAwaitManager(userId);}
        else if(data.equalsIgnoreCase("deleteManager")){
            userDataCache.setUserBotState(userId,BotState.DELETE_MANAGER);
            return getAnswerAwaitManager(userId);}
        else if(data.equalsIgnoreCase("exit")||data.equalsIgnoreCase("show")){
            userDataCache.setUserBotState(userId,BotState.MANAGE);
            if(data.equalsIgnoreCase("show")){
                return SendMessage.builder().chatId(userId)
                        .replyMarkup(manageKeyboardUtils.manageKeyboard())
                        .text("Список менеджеров\n"+
                                getManagers()).build();
            }
            return SendMessage.builder().chatId(userId)
                    .replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .text("Режим управления").build();}
        else{
            return SendMessage.builder().chatId(userId)
                    .text("Не корректная кнопка").build();
        }
    }

    private String getManagers(){
        List<ManagerModel> all = managerService.findAll();
        StringBuilder sb = new StringBuilder();
        for(var m: all){
            sb.append(m.getLastName()).append(" ").append(m.getFirstName()).append("\n");
        }
        return sb.toString();
    }

    private SendMessage getAnswerAwaitManager(Long chatId){
        return SendMessage.builder().chatId(chatId)
                .text(MessageUtils.AWAIT_MANAGER).build();
    }

}
