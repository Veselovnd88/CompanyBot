package ru.veselov.CompanyBot.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ManageKeyboardUtils {
    public static InlineKeyboardMarkup manageKeyboard(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var managers = new InlineKeyboardButton();
        managers.setCallbackData("managers");
        managers.setText("Менеджеры");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(managers);

        var divisions = new InlineKeyboardButton();
        managers.setCallbackData("divisions");
        managers.setText("Темы/отделы");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(divisions);

        var about = new InlineKeyboardButton();
        about.setCallbackData("about");
        about.setText("Ред. информацию о компании");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(divisions);

        var exit = new InlineKeyboardButton();
        exit.setText("Выход из режима управления");
        exit.setCallbackData("exit");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(exit);
        keyboard.add(row1); keyboard.add(row2); keyboard.add(row3); keyboard.add(row4);
        markup.setKeyboard(keyboard);
        return markup;
    }
}
