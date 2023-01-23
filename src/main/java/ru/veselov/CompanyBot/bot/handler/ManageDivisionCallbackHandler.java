package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.util.BotAnswerUtil;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

@Component
@Slf4j
public class ManageDivisionCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final DivisionService divisionService;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManageKeyboardUtils manageKeyboardUtils;

    public ManageDivisionCallbackHandler(UserDataCache userDataCache, DivisionService divisionService, DivisionKeyboardUtils divisionKeyboardUtils, ManageKeyboardUtils manageKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.divisionService = divisionService;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: нажата кнопка {}", userId,data);
        if(userDataCache.getUserBotState(userId)==BotState.DELETE_DIV){
            Division division = divisionKeyboardUtils.getKeyboardDivs().get(data);
            divisionService.remove(division);
            userDataCache.setUserBotState(userId,BotState.MANAGE);
            return SendMessage.builder().chatId(userId).replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .text("Режим управления").build();
        }
        switch (data){
            case "addDivision":
                userDataCache.setUserBotState(userId, BotState.AWAIT_DIVISION);
                return SendMessage.builder().chatId(userId)
                        .text(getAllDivisionsFormatted()
                                +MessageUtils.INPUT_DIV).build();
            case "deleteDivision":
                userDataCache.setUserBotState(userId,BotState.DELETE_DIV);
                return SendMessage.builder().chatId(userId)
                        .replyMarkup(divisionKeyboardUtils.getCustomerDivisionKeyboard())
                        .text("Выберите отдел для удаления").build();
            case "exit":
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(manageKeyboardUtils.manageKeyboard())
                        .build();
        }
        return BotAnswerUtil.getAnswerCallbackErrorMessage(update.getCallbackQuery().getId());
    }

    private String getAllDivisionsFormatted(){
        List<Division> all = divisionService.findAll();
        if(all.size()==0){
            return "Пока нет отделов\n";
        }
        else{
            StringBuilder sb = new StringBuilder();
            all.forEach(x-> sb.append(x.getDivisionId()).append(":").append(x.getName()).append("\n"));
            return sb.toString();
        }
    }

}
