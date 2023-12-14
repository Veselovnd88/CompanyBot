package ru.veselov.companybot.bot.keyboard.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.cache.Clearable;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DivisionKeyboardHelperImpl implements Clearable, DivisionKeyboardHelper {

    private Map<String, DivisionModel> idDivisionMapCache = new ConcurrentHashMap<>();

    private final DivisionServiceImpl divisionService;

    /**
     * Create keyboard with buttons based on divisions in db,
     * by default only one division for common questions
     * <p>
     * Iterate through the cache and create keyboard with callback data: id, text: description
     *
     * @return {@link InlineKeyboardMarkup} keyboard with divisions
     */
    @Override
    public InlineKeyboardMarkup getCustomerDivisionKeyboard() {
        List<DivisionModel> allDivisions = divisionService.findAll();
        if (allDivisions.isEmpty()) {
            DivisionModel baseDivision = DivisionModel.builder().divisionId(UUID.randomUUID())
                    .name("COMMON").description(MessageUtils.COMMON_DIV).build();
            allDivisions.add(baseDivision);
        }
        idDivisionMapCache = allDivisions.stream()
                .collect(Collectors.toMap(d -> d.getDivisionId().toString(), Function.identity()));
        log.debug("Divisions retrieved from DB and placed to id: Division cache");
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup(allDivisions);
        log.debug("Keyboard for divisions created");
        return markup;
    }


    /**
     * With this map we can get Division by from cache by its id
     *
     * @return Map<String, DivisionModel> copy of cache map with divisions
     */
    @Override
    public Map<String, DivisionModel> getCachedDivisions() {
        return Map.copyOf(idDivisionMapCache);
    }

    @Override
    public void clear(Long userId) {
        idDivisionMapCache.clear();
    }

    /**
     * Create keyboard with divisions from list
     *
     * @param allDivisions {@link List<DivisionModel>} list of division to set up as buttons
     * @return {@link InlineKeyboardMarkup} keyboard with divisions
     */
    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<DivisionModel> allDivisions) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (var d : allDivisions) {
            String description = d.getDescription();
            UUID callback = d.getDivisionId();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(description);
            button.setCallbackData(callback.toString());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        return markup;
    }

}
