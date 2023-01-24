package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ManageCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ManageKeyboardUtils manageKeyboardUtils;
    @Autowired
    public ManageCallbackHandler(UserDataCache userDataCache, ManageKeyboardUtils manageKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: нажата кнопка {}", userId, data);
        switch (data){
            case "managers":
                userDataCache.setUserBotState(userId, BotState.MANAGE_MANAGER);
                return SendMessage.builder().chatId(userId).text("Меню управления менеджерами")
                        .replyMarkup(managersManageKeyboard()).build();
            case "divisions":
                userDataCache.setUserBotState(userId, BotState.MANAGE_DIVISION);
                return SendMessage.builder().chatId(userId).text("Меню управления отделами")
                        .replyMarkup(divisionsManageKeyboard()).build();

            case "about":
                userDataCache.setUserBotState(userId, BotState.MANAGE_ABOUT);
                String currentInfo = MessageUtils.about.getText();
                return SendMessage.builder().chatId(userId)
                        .text("Пришлите новое описание компании, текущее: "+ currentInfo)
                        .entities(MessageUtils.entities).build();
            case "exit":
                userDataCache.setUserBotState(userId,BotState.READY);
                return SendMessage.builder().chatId(userId)
                        .text("Ожидаю команды")
                        .build();
        }
        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR)
                .build();
    }

    private InlineKeyboardMarkup managersManageKeyboard(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var save = new InlineKeyboardButton();
        save.setCallbackData("saveManager");
        save.setText("Добавить/редактировать менеджера");
        List<InlineKeyboardButton> row1= new ArrayList<>();
        row1.add(save);
        var delete = new InlineKeyboardButton();
        delete.setCallbackData("deleteManager");
        delete.setText("Удалить менеджера");
        List<InlineKeyboardButton> row2= new ArrayList<>();
        row2.add(delete);
        var exit = new InlineKeyboardButton();
        exit.setCallbackData("exit");
        exit.setText("Выход в главное меню");
        List<InlineKeyboardButton> row3= new ArrayList<>();
        row3.add(exit);
        keyboard.add(row1); keyboard.add(row2); keyboard.add(row3);
        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardMarkup divisionsManageKeyboard(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var save = new InlineKeyboardButton();
        save.setCallbackData("addDivision");
        save.setText("Добавить отдел/тему");
        List<InlineKeyboardButton> row1= new ArrayList<>();
        row1.add(save);
        var delete = new InlineKeyboardButton();
        delete.setCallbackData("deleteDivision");
        delete.setText("Удалить отдел/тему");
        List<InlineKeyboardButton> row2= new ArrayList<>();
        row2.add(delete);
        var exit = new InlineKeyboardButton();
        exit.setCallbackData("exit");
        exit.setText("Выход в главное меню");
        List<InlineKeyboardButton> row3= new ArrayList<>();
        row3.add(exit);
        keyboard.add(row1); keyboard.add(row2); keyboard.add(row3);
        markup.setKeyboard(keyboard);
        return markup;
    }
}
