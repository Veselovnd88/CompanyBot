package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class AddingDivisionFromKeyboardCallbackHandler implements UpdateHandler {
    /*This class handles callback from keyboard */
    @Value("${bot.adminId}")
    private Long adminId;
    private final UserDataCache userDataCache;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManagerService managerService;
    private final AdminCache adminCache;
    @Autowired
    public AddingDivisionFromKeyboardCallbackHandler(UserDataCache userDataCache, DivisionKeyboardUtils divisionKeyboardUtils, ManagerService managerService, AdminCache adminCache) {
        this.userDataCache = userDataCache;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.managerService = managerService;
        this.adminCache = adminCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        //Manager was forwarded and now waiting for adding him divisions
        EditMessageReplyMarkup editMessageReplyMarkup;
        try {
            editMessageReplyMarkup = divisionKeyboardUtils.divisionChooseField(update, data, adminCache.getManager(adminId).getId());
        } catch (NoDivisionsException e) {
            return SendMessage.builder().chatId(userId)
                    .text("Нет отделов, добавьте хотя бы 1").build();
        }
        List<String> possibleData = divisionKeyboardUtils.getPossibleButtons(editMessageReplyMarkup);
        log.info("{}: нажата кнопка {}",userId,data);
        if(possibleData.contains(data)) {
            return editMessageReplyMarkup;
        }
        if(data.equalsIgnoreCase("save")){
            Set<Division> markedDivisions = divisionKeyboardUtils.getMarkedDivisions(userId);
            User manager = adminCache.getManager(adminId);
            managerService.saveWithDivisions(manager,markedDivisions);
            log.info("{}: отделы менеджера обновлены", userId);
            adminCache.clear(adminId);
            userDataCache.setUserBotState(userId, BotState.READY);
            divisionKeyboardUtils.clear(adminId);
            return SendMessage.builder().chatId(userId)
                    .text(MessageUtils.MANAGER_SAVED)
                    .build();
        }
        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR)
                .build();//TODO throw NotSupportedUpdateException
    }
}
