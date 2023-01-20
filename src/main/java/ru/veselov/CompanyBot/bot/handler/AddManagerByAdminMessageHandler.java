package ru.veselov.CompanyBot.bot.handler;

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
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class AddManagerByAdminMessageHandler implements UpdateHandler {
    @Value("${bot.adminId}")
    private Long adminId;
    private final AdminCache adminCache;
    private final UserDataCache userDataCache;

    private final DivisionKeyboardUtils divisionKeyboardUtils;
    @Autowired
    public AddManagerByAdminMessageHandler(AdminCache adminCache, UserDataCache userDataCache, DivisionKeyboardUtils divisionKeyboardUtils) {
        this.adminCache = adminCache;
        this.userDataCache = userDataCache;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        if(update.getMessage().getForwardFrom()==null){
            log.info("{}: не содержит пересланного сообщения", userId);
            return SendMessage.builder().chatId(userId)
                    .text(MessageUtils.AWAIT_MANAGER).build();
        }
        User from = update.getMessage().getForwardFrom();
        adminCache.addManager(adminId,from);
        InlineKeyboardMarkup inlineKeyboardMarkup = divisionKeyboardUtils.getAdminDivisionKeyboard(from.getId());
        log.info("{}: принято пересланное сообщение от назначаемого менеджера", userId);
        userDataCache.setUserBotState(userId, BotState.ASSIGN_DIV);
        return SendMessage.builder().chatId(userId)
                .text(MessageUtils.AWAIT_DEPARTMENT)
                .replyMarkup(inlineKeyboardMarkup).build();
    }


}
