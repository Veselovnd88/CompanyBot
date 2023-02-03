package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionCallbackException;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.exception.NoDivisionKeyboardException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
public class AddDivisionToManagerFromCallbackHandler implements UpdateHandler {
    /*This class handles callback from keyboard */
    @Value("${bot.adminId}")
    private Long adminId;
    private final UserDataCache userDataCache;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManagerService managerService;
    private final AdminCache adminCache;
    @Autowired
    public AddDivisionToManagerFromCallbackHandler(UserDataCache userDataCache, DivisionKeyboardUtils divisionKeyboardUtils, ManagerService managerService, AdminCache adminCache) {
        this.userDataCache = userDataCache;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.managerService = managerService;
        this.adminCache = adminCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionCallbackException, NoAvailableActionSendMessageException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        //Manager was forwarded and now waiting for adding him divisions
        try {
            EditMessageReplyMarkup editMessageReplyMarkup = divisionKeyboardUtils.divisionChooseField(update, data);
            HashMap<String, DivisionModel> mapKeyboardDivisions = divisionKeyboardUtils.getMapKeyboardDivisions();
            log.info("{}: нажата кнопка {}",userId,data);
            if(mapKeyboardDivisions.containsKey(data)) {
                return editMessageReplyMarkup;
            }
            if(data.equalsIgnoreCase("save")){
                Set<DivisionModel> markedDivisions = divisionKeyboardUtils.getMarkedDivisions(userId);
                ManagerModel manager = adminCache.getManager(adminId);
                manager.setDivisions(markedDivisions);
                managerService.save(manager);
                log.info("{}: отделы менеджера обновлены", userId);
                adminCache.clear(adminId);
                userDataCache.setUserBotState(userId, BotState.READY);
                divisionKeyboardUtils.clear(adminId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.MANAGER_SAVED)
                        .build();
            }
            throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION,
                    update.getCallbackQuery().getId());
        }
        catch (NoDivisionsException | NoDivisionKeyboardException e) {
            throw new NoAvailableActionSendMessageException(e.getMessage(), userId.toString(),
                    e);
        }
    }
}
