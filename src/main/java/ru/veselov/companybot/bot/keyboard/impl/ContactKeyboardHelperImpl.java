package ru.veselov.companybot.bot.keyboard.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.keyboard.ContactKeyboardHelper;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for creating and working with keyboard responsible to input contact data by user
 */
@Component
@Slf4j
public class ContactKeyboardHelperImpl implements ContactKeyboardHelper {

    private static final String LEFT_MARK = "<<";

    private static final String RIGHT_MARK = ">>";

    private static final String WHITE_CHECK_MARK = ":white_check_mark:";

    //storing keyboard as edit message markup  for every user
    private final Map<Long, EditMessageReplyMarkup> keyboardMessageCache = new ConcurrentHashMap<>();

    //contains buttons by raw, need for mark/unmark button
    private final Map<String, Integer> rowIndexesByFieldName = new ConcurrentHashMap<>();

    /**
     * Filling internal map with indexes of rows/buttons indexes
     */
    public ContactKeyboardHelperImpl() {
        rowIndexesByFieldName.put(CallBackButtonUtils.NAME, 0);
        rowIndexesByFieldName.put(CallBackButtonUtils.EMAIL, 1);
        rowIndexesByFieldName.put(CallBackButtonUtils.PHONE, 2);
        rowIndexesByFieldName.put(CallBackButtonUtils.SHARED, 3);
    }

    /**
     * Creates keyboard with buttons for invitation user to input contact data
     *
     * @return {@link InlineKeyboardMarkup} keyboard with input field names
     */
    @Override
    public InlineKeyboardMarkup getContactKeyboard() {
        var markup = new InlineKeyboardMarkup();
        var inputName = new InlineKeyboardButton();
        inputName.setText(MessageUtils.INPUT_FIO);
        inputName.setCallbackData(CallBackButtonUtils.NAME);
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(inputName);
        InlineKeyboardButton inputEmail = new InlineKeyboardButton();
        inputEmail.setText(MessageUtils.INPUT_EMAIL);
        inputEmail.setCallbackData(CallBackButtonUtils.EMAIL);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(inputEmail);
        var inputPhone = new InlineKeyboardButton();
        inputPhone.setText(MessageUtils.INPUT_PHONE);
        inputPhone.setCallbackData(CallBackButtonUtils.PHONE);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(inputPhone);
        var inputContact = new InlineKeyboardButton();
        inputContact.setText(MessageUtils.ATTACH_CONTACT);
        inputContact.setCallbackData(CallBackButtonUtils.SHARED);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(inputContact);
        var save = new InlineKeyboardButton();
        save.setText(MessageUtils.SAVE_AND_SEND);
        save.setCallbackData(CallBackButtonUtils.SAVE);
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(save);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        markup.setKeyboard(keyboard);
        log.debug("Keyboard with contact input button created");
        return markup;
    }

    /**
     * Get contact keyboard {@link InlineKeyboardMarkup} from cache or create a new one if not exists,
     * <p>mark chosen field with << FIELD >>, or remove this mark after pressing another button
     * </p>
     *
     * @param update {@link Update} from Telegram
     * @param field  {@link String} field name data from {@link CallbackQuery}
     * @return {@link EditMessageReplyMarkup} keyboard with marked button
     */
    @Override
    public EditMessageReplyMarkup getEditMessageReplyForChosenCallbackButton(Update update, String field) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        if (keyboardMessageCache.containsKey(userId)) {
            inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        } else {
            inlineKeyboardMarkup = getContactKeyboard();
        }
        for (var keyboard : inlineKeyboardMarkup.getKeyboard()) {
            if (keyboard.get(0).getText().startsWith(LEFT_MARK)) {
                keyboard.get(0).setText(removeBracers(keyboard.get(0).getText()));
                log.debug("Button unmarked");
            }
        }
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(getRowIndexByFieldName(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        inlineKeyboardButton.setText(LEFT_MARK + inlineKeyboardButton.getText() + RIGHT_MARK);
        log.debug("Mark chosen field for [callback: {}] input", field);
        Message message = update.getCallbackQuery().getMessage();
        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        keyboardMessageCache.put(userId, editedKeyboard);
        return editedKeyboard;
    }

    /**
     * Get {@link EditMessageReplyMarkup} after input contact data by user, change current message with
     * keyboard after receiving text message with contact data
     *
     * @param userId {@link Long} id of user who send message
     * @param field  {@link String} field name data from {@link CallbackQuery}
     * @return {@link EditMessageReplyMarkup} keyboard with marked button
     */
    @Override
    public EditMessageReplyMarkup getEditMessageReplyAfterSendingContactData(Long userId, String field) {
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(getRowIndexByFieldName(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        String buttonText = inlineKeyboardButton.getText();
        String newText = removeBracers(buttonText);
        if (!EmojiParser.parseToAliases(newText).startsWith(WHITE_CHECK_MARK)) {
            inlineKeyboardButton.setText(EmojiParser.parseToUnicode(WHITE_CHECK_MARK + newText));
        }
        return keyboardMessageCache.get(userId);
    }

    /**
     * Clear keyboard cache after
     *
     * @param userId {@link Long} id of user
     */
    @Override
    public void clear(Long userId) {
        log.debug("Contact keyboard removed for [user id: {}]", userId);
        keyboardMessageCache.remove(userId);
    }

    /**
     * Get row index (number) by field name, for more convenient access
     *
     * @param field {@link String} field name from {@link CallbackQuery}
     * @return {@link Integer} number of passed button name
     */
    private Integer getRowIndexByFieldName(String field) {
        return rowIndexesByFieldName.get(field);
    }

    /**
     * Remove bracers << or >> from marked button
     *
     * @param text {@link String} text from button
     * @return {@link String} cleared text of button
     */
    private String removeBracers(String text) {
        String replaceOne = text.replace("<", "").replace("<", "");
        return replaceOne.replace(">", "").replace(">", "");
    }

}

