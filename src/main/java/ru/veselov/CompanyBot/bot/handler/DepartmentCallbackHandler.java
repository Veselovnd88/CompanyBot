package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.Department;

@Component
@Slf4j
public class DepartmentCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    @Autowired
    public DepartmentCallbackHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        Department department = null;
        switch (data){
            case "leuze":
                department=Department.LEUZE;
                break;
            case "lpkf":
                department=Department.LPKF;
                break;
            case "pressure":
                department=Department.PRESSURE;
                break;
            case "common":
                department=Department.COMMON;
                break;
        }
        if(department!=null){
            userDataCache.createInquiry(userId,department);
            userDataCache.setUserBotState(userId, BotState.AWAIT_MESSAGE);
            return SendMessage.builder().chatId(userId)
                    .text("Введите ваш вопрос или перешлите мне сообщение").build();
        }


        return SendMessage.builder().chatId(userId)
                .text("Произошла ошибка").build();
    }
}
