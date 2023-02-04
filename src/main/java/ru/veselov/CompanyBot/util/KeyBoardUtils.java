package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.cache.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class KeyBoardUtils implements Cache {
    private final HashMap<Long,EditMessageReplyMarkup> keyboardMessageCache = new HashMap<>();

    private final HashMap<String,Integer> rowsIndexes = new HashMap<>();

    public KeyBoardUtils(){
        rowsIndexes.put("name",0);
        rowsIndexes.put("email",1);
        rowsIndexes.put("phone",2);
        rowsIndexes.put("shared",3);
    }
    public InlineKeyboardMarkup contactKeyBoard(){
        var markup = new InlineKeyboardMarkup();
        var inputName = new InlineKeyboardButton();
        inputName.setText("Ввести ФИО");
        inputName.setCallbackData("name");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(inputName);
        InlineKeyboardButton inputEmail = new InlineKeyboardButton();
        inputEmail.setText("Ввести email");
        inputEmail.setCallbackData("email");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(inputEmail);
        var inputPhone = new InlineKeyboardButton();
        inputPhone.setText("Ввести номер телефона");
        inputPhone.setCallbackData("phone");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(inputPhone);
        var inputContact = new InlineKeyboardButton();
        inputContact.setText("Прикрепить контакт");
        inputContact.setCallbackData("shared");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(inputContact);
        var save=new InlineKeyboardButton();
        save.setText("Сохранить и отправить контактные данные");
        save.setCallbackData("save");
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(save);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        markup.setKeyboard(keyboard);
        return markup;
    }

    public EditMessageReplyMarkup editMessageChooseField(Update update, String field){
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        if(keyboardMessageCache.containsKey(userId)){
            inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        }
        else{
            inlineKeyboardMarkup = contactKeyBoard();
        }
        for(var keyboard: inlineKeyboardMarkup.getKeyboard()){
            if(keyboard.get(0).getText().startsWith("<<")){
                keyboard.get(0).setText(removeBracers(keyboard.get(0).getText()));
            }
        }
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(rowIndex(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        inlineKeyboardButton.setText("<<"+inlineKeyboardButton.getText()+">>");

        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        keyboardMessageCache.put(userId,editedKeyboard);
        return editedKeyboard;
    }

    public EditMessageReplyMarkup editMessageSavedField(Long userId, String field){
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMessageCache.get(userId).getReplyMarkup();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(rowIndex(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        String buttonText = inlineKeyboardButton.getText();
        String newText = removeBracers(buttonText);
        if(!EmojiParser.parseToAliases(newText).startsWith(":white_check_mark:")){
            inlineKeyboardButton.setText(EmojiParser.parseToUnicode(":white_check_mark:"+newText));}
        return keyboardMessageCache.get(userId);
    }

    private int rowIndex(String field){
        return rowsIndexes.get(field);
    }

    private String removeBracers(String string){
        String replaceOne = string.replace("<", "").replace("<","");
        return replaceOne.replace(">", "").replace(">","");
    }


    @Override
    public void clear(Long userId) {
        log.info("{}: клавиатура кнопок для контактов удалена",userId);
        keyboardMessageCache.remove(userId);
    }
    @Profile("test")
    public HashMap<Long,EditMessageReplyMarkup> getKeyboardMessageCache(){
        return keyboardMessageCache;
    }

    @Profile("test")
    public int getRowIndexForTest(String string){
        return rowIndex(string);
    }
}
