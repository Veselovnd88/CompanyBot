package ru.veselov.companybot.bot.keyboard;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.cache.Clearable;

public interface ContactKeyboardHelper extends Clearable {

    InlineKeyboardMarkup contactKeyBoard();

    EditMessageReplyMarkup editMessageChooseField(Update update, String field);

    EditMessageReplyMarkup editMessageSavedField(Long userId, String field);

}
