package ru.veselov.companybot.bot.keyboard;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.exception.NoDivisionKeyboardException;
import ru.veselov.companybot.model.DivisionModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface DivisionKeyboardHelper {
    InlineKeyboardMarkup getCustomerDivisionKeyboard();

    EditMessageReplyMarkup divisionChooseField(Update update, String field) throws NoDivisionKeyboardException;

    Set<DivisionModel> getMarkedDivisions(Long userId);

    HashMap<String, DivisionModel> getMapKeyboardDivisions();

    Map<String, DivisionModel> getCachedDivisions();
}
