package ru.veselov.companybot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ManageKeyboardUtils {
    public InlineKeyboardMarkup manageKeyboard(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var managers = new InlineKeyboardButton();
        managers.setCallbackData("managers");
        managers.setText("Менеджеры");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(managers);

        var divisions = new InlineKeyboardButton();
        divisions.setCallbackData("divisions");
        divisions.setText("Темы/отделы");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(divisions);

        var about = new InlineKeyboardButton();
        about.setCallbackData("about");
        about.setText("Ред. информацию о компании");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(about);

        var exit = new InlineKeyboardButton();
        exit.setText("Выход из режима управления");
        exit.setCallbackData("exit");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(exit);
        keyboard.add(row1); keyboard.add(row2); keyboard.add(row3); keyboard.add(row4);
        markup.setKeyboard(keyboard);
        return markup;
    }


    public InlineKeyboardMarkup managersManageKeyboard(){
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

    public InlineKeyboardMarkup divisionsManageKeyboard(){
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
