package ru.veselov.companybot.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.model.DivisionModel;

import java.util.Map;

public interface DivisionKeyboardHelper {
    InlineKeyboardMarkup getCustomerDivisionKeyboard();

    Map<String, DivisionModel> getCachedDivisions();

}
