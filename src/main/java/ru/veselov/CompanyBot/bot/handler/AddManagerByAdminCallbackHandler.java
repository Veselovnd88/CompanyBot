package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
public class AddManagerByAdminCallbackHandler implements UpdateHandler {
    @Value("${bot.adminId}")
    private Long adminId;
    private final DivisionService divisionService;
    private final UserDataCache userDataCache;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManagerService managerService;
    private final AdminCache adminCache;
    @Autowired
    public AddManagerByAdminCallbackHandler(DivisionService divisionService, UserDataCache userDataCache, DivisionKeyboardUtils divisionKeyboardUtils, ManagerService managerService, AdminCache adminCache) {
        this.divisionService = divisionService;
        this.userDataCache = userDataCache;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.managerService = managerService;
        this.adminCache = adminCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        HashMap<String, Division> keyboardDivs = divisionKeyboardUtils.getKeyboardDivs();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: нажата кнопка {}",userId,data);
        if(keyboardDivs.containsKey(data)) {
            return divisionKeyboardUtils.divisionChooseField(update, data);
        }

        if(data.equalsIgnoreCase("save")){
            Set<Division> markedDivisions = divisionKeyboardUtils.getMarkedDivisions(userId);
            User manager = adminCache.getManager(adminId);
            managerService.saveWithDivisions(manager,markedDivisions);
            log.info("{}: отделы менеджера обновлены", userId);
            userDataCache.setUserBotState(userId, BotState.READY);
            return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                    .text(MessageUtils.SAVED)
                    .build();
        }
        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR)
                .build();
    }
}
