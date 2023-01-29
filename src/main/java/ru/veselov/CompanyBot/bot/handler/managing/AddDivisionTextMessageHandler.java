package ru.veselov.CompanyBot.bot.handler.managing;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class AddDivisionTextMessageHandler implements UpdateHandler {
    private final DivisionService divisionService;
    private final UserDataCache userDataCache;
    private final ManageKeyboardUtils manageKeyboardUtils;

    public AddDivisionTextMessageHandler(DivisionService divisionService, UserDataCache userDataCache, ManageKeyboardUtils manageKeyboardUtils) {
        this.divisionService = divisionService;
        this.userDataCache = userDataCache;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String text = update.getMessage().getText();
        if(!isCorrectInput(text)){
            return SendMessage.builder().chatId(userId).text("Не корректные ввод\n"+
                    MessageUtils.INPUT_DIV).build();
        }
        else{
            String[] split = text.split(" ");
            DivisionModel division = DivisionModel.builder().divisionId(split[0])
                    .name(text.substring(split[0].length())).build();
            divisionService.save(division);
            userDataCache.setUserBotState(userId, BotState.MANAGE);
            return SendMessage.builder().chatId(userId)
                    .text("Режим управления").replyMarkup(manageKeyboardUtils.manageKeyboard())
                    .build();
        }
    }

    private boolean isCorrectInput(String input){
        String[] split = input.split(" ");
        if(split.length<2||split[0].length()!=2){
            return false;
        }
        else return input.substring(3).length() <= 40;
    }
}
