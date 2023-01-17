package ru.veselov.CompanyBot.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        InlineKeyboardButton noDivisionButton = new InlineKeyboardButton();
        noDivisionButton.setCallbackData("none");
        noDivisionButton.setText("Отписать от направлений");
        InlineKeyboardButton saveButton = new InlineKeyboardButton();
        saveButton.setCallbackData("save");
        saveButton.setText("Сохранить");
        keyboard.add(List.of(noDivisionButton));
        keyboard.add(List.of(saveButton));

        markup.setKeyboard(keyboard);
        return markup;
    }


    public EditMessageReplyMarkup divisionChooseField(Update update, String field){
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        //Отдаем чистую клавиатуру, или достаем из кеша
        if(departmentKeyboardCache.containsKey(userId)){
            inlineKeyboardMarkup = departmentKeyboardCache.get(userId).getReplyMarkup();
        }
        else{
            inlineKeyboardMarkup = departmentKeyboard();
        }
        if(field.equalsIgnoreCase("none")){
            for(var keyboard :inlineKeyboardMarkup.getKeyboard()){
                keyboard.get(0).setText(removeMark(keyboard.get(0).getText()));
            }
        }
        else{
            for(var keyboard: inlineKeyboardMarkup.getKeyboard()){
                //находим кнопку на которую нажали
                if(keyboard.get(0).getText().equalsIgnoreCase(field)){
                    //если она уже была помечена, то снимаем галочку
                    if(isMarked(keyboard.get(0).getText())){
                        keyboard.get(0).setText(removeMark(keyboard.get(0).getText()));
                    }
                    else{
                        keyboard.get(0).setText(EmojiParser.parseToUnicode(
                                ":white_check_mark:"+keyboard.get(0).getText()));
                    }
                }
            }
        }
        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        departmentKeyboardCache.put(userId,editedKeyboard);
        return editedKeyboard;
    }


    private String removeMark(String string){
        return EmojiParser.parseToAliases(string).replace(":white_check_mark:","");
    }
    public boolean isMarked(String text){
        return EmojiParser.parseToAliases(text).startsWith(":white_check_mark:");
    }




    @Override
    public void clear(Long userId) {
        //FIXME clear
    }
}
