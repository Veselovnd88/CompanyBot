package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class ManageDivisionMessageHandler implements UpdateHandler {
    private final DivisionService divisionService;

    public ManageDivisionMessageHandler(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String text = update.getMessage().getText();
        String[] split = text.split(" ");
        if(split.length<2&& split[0].length()<2){
            return SendMessage.builder().chatId(userId).text("Не корректные ввод\n"+
                    MessageUtils.INPUT_DIV).build();
        }
        else{
            Division division = Division.builder().divisionId(split[0])
                    .name(text.substring(split[0].length())).build();
            divisionService.save(division);
        }


        return null;
    }
}
