package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.veselov.CompanyBot.cache.Cache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class DivisionKeyboardUtils implements Cache {//FIXME возможно есть смысл сделать общего предка у клавиатурных классов

    private final DivisionService divisionService;


    private final HashMap<Long, EditMessageReplyMarkup> departmentKeyboardCache = new HashMap<>();
    @Autowired
    public DivisionKeyboardUtils(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    public InlineKeyboardMarkup departmentKeyboard(){
        List<Division> allDivisions = divisionService.findAll();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for(var d: allDivisions){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(d.getName());
            button.setCallbackData(d.getDivisionId());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        return markup;
    }


    public EditMessageReplyMarkup divisionChooseField(Update update, String field){
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        if(departmentKeyboardCache.containsKey(userId)){
            inlineKeyboardMarkup = departmentKeyboardCache.get(userId).getReplyMarkup();
        }
        else{
            inlineKeyboardMarkup = departmentKeyboard();
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
        departmentKeyboardCache.put(userId,editedKeyboard);
        return editedKeyboard;
    }

    public EditMessageReplyMarkup editMessageSavedField(Long userId, String field){
        InlineKeyboardMarkup inlineKeyboardMarkup = departmentKeyboardCache.get(userId).getReplyMarkup();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(rowIndex(field));
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        String buttonText = inlineKeyboardButton.getText();
        String newText = removeBracers(buttonText);
        if(!EmojiParser.parseToAliases(newText).startsWith(":white_check_mark:")){
            inlineKeyboardButton.setText(EmojiParser.parseToUnicode(":white_check_mark:"+newText));}
        return keyboardMessageCache.get(userId);
    }


    private String removeBracers(String string){
        String replaceOne = string.replace("<", "").replace("<","");
        return replaceOne.replace(">", "").replace(">","");
    }


    public boolean isMarked(InlineKeyboardMarkup markup, String field){
        List<List<InlineKeyboardButton>> keyboard = markup.getKeyboard();
        for(var row: keyboard){
            if(row.get(0).getText().contains(field)){
                return EmojiParser.parseToAliases(row.get(0).getText()).startsWith(":white_check_mark:");
            }
        }
        return false;
    }




    @Override
    public void clear(Long userId) {
        //FIXME clear
    }
}
