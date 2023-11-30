package ru.veselov.companybot.bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyBoardUtils {

    public static InlineKeyboardMarkup setUpContactInlineKeyboard(String text) {
        InlineKeyboardButton finishMessages = new InlineKeyboardButton();
        finishMessages.setText(text);
        finishMessages.setCallbackData("contact");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(finishMessages);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
