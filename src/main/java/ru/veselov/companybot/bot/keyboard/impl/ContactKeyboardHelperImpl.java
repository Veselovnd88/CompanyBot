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

@Component
@Slf4j
public class ContactKeyboardHelperImpl implements ContactKeyboardHelper {

    private static final String LEFT_MARK = "<<";
    private static final String RIGHT_MARK = ">>";
    private static final String WHITE_CHECK_MARK = ":white_check_mark:";

    private final Map<Long, EditMessageReplyMarkup> keyboardMessageCache = new ConcurrentHashMap<>();

    //contains buttons by raw, need for mark/unmark button
    private final Map<String, Integer> rowsIndexes = new ConcurrentHashMap<>();

    /**
     * Filling internal map with indexes of rows/buttons indexes
     */
    public ContactKeyboardHelperImpl() {
        rowsIndexes.put(CallBackButtonUtils.NAME, 0);
        rowsIndexes.put(CallBackButtonUtils.EMAIL, 1);
        rowsIndexes.put(CallBackButtonUtils.PHONE, 2);
        rowsIndexes.put(CallBackButtonUtils.SHARED, 3);
    }

    @Override
    public InlineKeyboardMarkup contactKeyBoard() {
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
     * Takes contact keyboard {@link InlineKeyboardMarkup} from cache or create a new one if not exists, mark chosen
     * field with << FIELD >>, or remove this mark if this button was pressed second time
     *
     * @param update update from Telegram {@link Update}
     * @param field  data from {@link CallbackQuery}
     * @return {@link EditMessageReplyMarkup} edited telegram message with keyboard
     */
    @Override
    public EditMessageReplyMarkup editMessageChooseField(Update update, String field) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        if (keyboardMessageCache.containsKey(userId)) {
            inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        } else {
            inlineKeyboardMarkup = contactKeyBoard();
        }
        for (var keyboard : inlineKeyboardMarkup.getKeyboard()) {
            if (keyboard.get(0).getText().startsWith(LEFT_MARK)) {
                keyboard.get(0).setText(removeBracers(keyboard.get(0).getText()));
                log.debug("Button unmarked");
            }
        }
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(rowIndex(field));
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

    @Override
    public EditMessageReplyMarkup editMessageSavedField(Long userId, String field) {
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(rowIndex(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        String buttonText = inlineKeyboardButton.getText();
        String newText = removeBracers(buttonText);
        if (!EmojiParser.parseToAliases(newText).startsWith(WHITE_CHECK_MARK)) {
            inlineKeyboardButton.setText(EmojiParser.parseToUnicode(WHITE_CHECK_MARK + newText));
        }
        return keyboardMessageCache.get(userId);
    }

    private int rowIndex(String field) {
        return rowsIndexes.get(field);
    }

    private String removeBracers(String string) {
        String replaceOne = string.replace("<", "").replace("<", "");
        return replaceOne.replace(">", "").replace(">", "");
    }


    @Override
    public void clear(Long userId) {
        log.debug("Contact keyboard removed for [user id: {}]", userId);
        keyboardMessageCache.remove(userId);
    }

}

