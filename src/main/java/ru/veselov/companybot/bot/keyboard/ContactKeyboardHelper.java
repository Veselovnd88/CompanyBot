package ru.veselov.companybot.bot.keyboard;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.cache.Clearable;

public interface ContactKeyboardHelper extends Clearable {

    InlineKeyboardMarkup getNewContactKeyboard();

    EditMessageReplyMarkup getEditMessageReplyForChosenCallbackButton(Update update, String field);

    EditMessageReplyMarkup getEditMessageReplyAfterSendingContactData(Long userId, String field);

    InlineKeyboardMarkup getInviteInputContactKeyboard(String message);

    EditMessageReplyMarkup getCurrentContactKeyboard(Long userId);

}
