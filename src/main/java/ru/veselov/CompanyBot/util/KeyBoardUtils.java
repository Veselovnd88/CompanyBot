package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class KeyBoardUtils {
    private final HashMap<Long,EditMessageReplyMarkup> keyboardMessageCache = new HashMap<>();
    public InlineKeyboardMarkup contactKeyBoard(){
        var inputName = new InlineKeyboardButton();
        inputName.setText("Введите ФИО");
        inputName.setCallbackData("name");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(inputName);
        InlineKeyboardButton inputEmail = new InlineKeyboardButton();
        inputEmail.setText("Ввести email");
        inputEmail.setCallbackData("email");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(inputEmail);
        var inputPhone = new InlineKeyboardButton();
        inputPhone.setText("Ввести номер телефона (c +");
        inputPhone.setCallbackData("phone");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(inputPhone);
        var inputContact = new InlineKeyboardButton();
        inputContact.setText("Прикрепите контакт");
        inputContact.setCallbackData("shared");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(inputContact);
        var save=new InlineKeyboardButton();
        save.setText("Сохранить контактные данные");
        save.setCallbackData("save");
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(save);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        var markup = new InlineKeyboardMarkup();
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
        switch (field){
            case "name":
                return 0;
            case "email":
                return 1;
            case "phone":
                return 2;
            case "shared":
                return 3;
            default:
                return -1;
        }
    }

    private String removeBracers(String string){
        return string.substring(2,string.length()-2);
    }
    @Profile("test")
    public int getRowIndexForTest(String string){
        return rowIndex(string);
    }

    @Profile("test")
    public HashMap<Long,EditMessageReplyMarkup> getKeyboardMessageCache(){
        return keyboardMessageCache;
    }

}
