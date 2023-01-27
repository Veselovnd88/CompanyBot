package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.exception.NoSuchManagerException;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class AddingManagerMessageHandler implements UpdateHandler {
    @Value("${bot.adminId}")
    private Long adminId;
    private final AdminCache adminCache;
    private final UserDataCache userDataCache;
    private final ManagerService managerService;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManageKeyboardUtils manageKeyboardUtils;
    @Autowired
    public AddingManagerMessageHandler(AdminCache adminCache, UserDataCache userDataCache, ManagerService managerService, DivisionKeyboardUtils divisionKeyboardUtils, ManageKeyboardUtils manageKeyboardUtils) {
        this.adminCache = adminCache;
        this.userDataCache = userDataCache;
        this.managerService = managerService;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCache.getUserBotState(userId);
        if(update.getMessage().getForwardFrom()==null){
            log.info("{}: не содержит пересланного сообщения", userId);
            return SendMessage.builder().chatId(userId)
                    .text(MessageUtils.AWAIT_MANAGER).build();
        }
        User from = update.getMessage().getForwardFrom();
        if(BotState.AWAIT_MANAGER==botState){
            try {
                InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.getAdminDivisionKeyboard(userId, from.getId());
                log.info("{}: принято пересланное сообщение от назначаемого менеджера", userId);
                userDataCache.setUserBotState(userId, BotState.ASSIGN_DIV);
                ManagerModel managerModel = ManagerModel.builder().managerId(from.getId()).firstName(from.getFirstName())
                        .lastName(from.getLastName()).userName(from.getUserName()).build();
                adminCache.addManager(adminId,managerModel);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.AWAIT_DEPARTMENT)
                        .replyMarkup(inlineKeyboardMarkup).build();

            } catch (NoDivisionsException e) {
                return SendMessage.builder().chatId(userId)
                        .text(e.getMessage()).build();
            }
        }
        if(BotState.DELETE_MANAGER==botState){
            managerService.remove(ManagerModel.builder().managerId(from.getId()).build());
            userDataCache.setUserBotState(userId,BotState.MANAGE);
            return SendMessage.builder().chatId(userId)
                    .text(MessageUtils.MANAGER_DELETED).replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .build();
        }
        return null;//FIXME no null return
    }


}
