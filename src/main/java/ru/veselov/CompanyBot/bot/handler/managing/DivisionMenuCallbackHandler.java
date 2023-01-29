package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.util.BotAnswerUtil;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

@Component
@Slf4j
public class DivisionMenuCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final DivisionService divisionService;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManageKeyboardUtils manageKeyboardUtils;

    private final BotAnswerUtil botAnswerUtil;

    public DivisionMenuCallbackHandler(UserDataCache userDataCache, DivisionService divisionService, DivisionKeyboardUtils divisionKeyboardUtils, ManageKeyboardUtils manageKeyboardUtils, BotAnswerUtil botAnswerUtil) {
        this.userDataCache = userDataCache;
        this.divisionService = divisionService;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.manageKeyboardUtils = manageKeyboardUtils;
        this.botAnswerUtil = botAnswerUtil;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: нажата кнопка {}", userId,data);
        if(userDataCache.getUserBotState(userId)==BotState.DELETE_DIV){
            userDataCache.setUserBotState(userId,BotState.MANAGE);
            String message;
            try {
                DivisionModel divisionModel = divisionKeyboardUtils.getMapKeyboardDivisions().get(data);
                divisionService.remove(divisionModel);
                message="Режим управления";

            } catch (NoDivisionsException e) {
                message=e.getMessage();
            }
            return SendMessage.builder().chatId(userId).replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .text(message).build();
        }
        switch (data){
            case "addDivision":
                userDataCache.setUserBotState(userId, BotState.AWAIT_DIVISION);
                return SendMessage.builder().chatId(userId)
                        .text(getAllDivisionsFormatted()
                                +MessageUtils.INPUT_DIV).build();
            case "deleteDivision":
                userDataCache.setUserBotState(userId,BotState.DELETE_DIV);
                try {
                    return SendMessage.builder().chatId(userId)
                            .replyMarkup(divisionKeyboardUtils.getCustomerDivisionKeyboard())
                            .text("Выберите отдел для удаления").build();
                } catch (NoDivisionsException e) {
                    userDataCache.setUserBotState(userId,BotState.MANAGE);
                    return SendMessage.builder().chatId(userId).replyMarkup(manageKeyboardUtils.manageKeyboard())
                            .text(e.getMessage()+"\nВозврат в режим управления").build();
                }
            case "exit":
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(manageKeyboardUtils.manageKeyboard())
                        .build();
        }
        return botAnswerUtil.getAnswerCallbackErrorMessage(update.getCallbackQuery().getId());
    }

    private String getAllDivisionsFormatted(){
        List<DivisionModel> all = divisionService.findAll();
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
