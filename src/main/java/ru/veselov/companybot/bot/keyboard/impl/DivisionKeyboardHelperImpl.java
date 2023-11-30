package ru.veselov.companybot.bot.keyboard.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.Cache;
import ru.veselov.companybot.exception.EmptyDivisionsException;
import ru.veselov.companybot.exception.NoDivisionKeyboardException;
import ru.veselov.companybot.exception.NoDivisionsException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DivisionKeyboardHelperImpl implements Cache, DivisionKeyboardHelper {

    private static final String MARKED = "+marked";

    private static final String EMOJI_MARK = ":white_check_mark:";

    private final Map<UUID, DivisionModel> idToDivision = new HashMap<>();

    private final Map<Long, InlineKeyboardMarkup> divisionKeyboardCache = new ConcurrentHashMap<>();

    private final DivisionServiceImpl divisionService;


    @Override
    public InlineKeyboardMarkup getCustomerDivisionKeyboard() {
        List<DivisionModel> allDivisions = refreshDivisions();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (var d : allDivisions) {
            String name = d.getName();
            UUID callback = d.getDivisionId();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(name);
            button.setCallbackData(callback.toString());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        return markup;
    }


    @Override
    public EditMessageReplyMarkup divisionChooseField(Update update, String field) throws NoDivisionKeyboardException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        //Create new keyboard or send it from our cache
        if (divisionKeyboardCache.containsKey(userId)) {
            inlineKeyboardMarkup = divisionKeyboardCache.get(userId);
        } else {
            throw new NoDivisionKeyboardException();
        }
        for (var keyboard : inlineKeyboardMarkup.getKeyboard()) {
            //finding pressed button, but we don't need to mark save button
            if (keyboard.get(0).getCallbackData().equalsIgnoreCase(field)
                    && !keyboard.get(0).getCallbackData().equalsIgnoreCase("save")) {
                if (isMarked(keyboard.get(0).getCallbackData())) {
                    removeMark(keyboard.get(0));
                } else {
                    keyboard.get(0).setText(EmojiParser.parseToUnicode(EMOJI_MARK + keyboard.get(0).getText()));
                    keyboard.get(0).setCallbackData(keyboard.get(0).getCallbackData() + MARKED);
                }
            }
        }
        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        divisionKeyboardCache.put(userId, inlineKeyboardMarkup);
        return editedKeyboard;
    }

    @Override
    public Set<DivisionModel> getMarkedDivisions(Long userId) {
        HashSet<DivisionModel> divNames = new HashSet<>();
        InlineKeyboardMarkup editMessageReplyMarkup = divisionKeyboardCache.get(userId);
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getKeyboard();
        for (var row : keyboard) {
            if (isMarked(row.get(0).getCallbackData())) {
                divNames.add(getDivisionByName(row.get(0).getCallbackData().replace(MARKED, "")));
            }
        }
        return divNames;
    }

    @Override
    public HashMap<String, DivisionModel> getMapKeyboardDivisions() {
        if (idToDivision.isEmpty()) {
            refreshDivisions();
        }
        HashMap<String, DivisionModel> withMarked = new HashMap<>();
        idToDivision.forEach((x, y) -> {
            withMarked.put(String.valueOf(x), y);
            withMarked.put(x + MARKED, y);
        });
        return withMarked;
    }

    @Override
    public Map<UUID, DivisionModel> getCachedDivisions() throws NoDivisionsException {
        if (idToDivision.isEmpty()) {
            throw new NoDivisionsException();
        }
        /*We need it for showing possible divisions on keyboard buttons*/
        return Map.copyOf(idToDivision);
    }

    @Override
    public void clear(Long userId) {
        divisionKeyboardCache.remove(userId);
        idToDivision.clear();
    }

    private List<DivisionModel> refreshDivisions() {
        //After creation of keyboard all divisions placed in cache
        List<DivisionModel> allDivisions = divisionService.findAll();
        if (allDivisions.isEmpty()) {
            log.warn("Empty divisions list, error occurred");
            throw new EmptyDivisionsException("There is no divisions at the moment, please try later");
        }
        for (var d : allDivisions) {
            idToDivision.put(d.getDivisionId(), d);
        }
        return allDivisions;
    }

    private DivisionModel getDivisionByName(String name) {
        return idToDivision.get(name);
    }

    private void removeMark(InlineKeyboardButton button) {
        button.setText(EmojiParser.parseToAliases(button.getText()).replace(EMOJI_MARK, ""));
        button.setCallbackData(button.getCallbackData().replace(MARKED, ""));
    }

    private boolean isMarked(String text) {
        return text.endsWith(MARKED);
    }
}
