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

import java.util.*;

@Component
@Slf4j
public class DivisionKeyboardUtils implements Cache {//FIXME возможно есть смысл сделать общего предка у клавиатурных классов

    private final DivisionService divisionService;
    private final HashMap<String, Division> nameToDivision =new HashMap<>();

    private final String mark="+marked";

    private final HashMap<Long, EditMessageReplyMarkup> divisionKeyboardCache = new HashMap<>();
    @Autowired
    public DivisionKeyboardUtils(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    public InlineKeyboardMarkup divisionKeyboard(){
        //При создании клавиатуры забираются все отделы и помещаются в кеш
        List<Division> allDivisions = divisionService.findAll();
        for(var d: allDivisions){
            nameToDivision.put(d.getDivisionId(),d);
        }
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

        InlineKeyboardButton saveButton = new InlineKeyboardButton();
        saveButton.setCallbackData("save");
        saveButton.setText("Сохранить");
        keyboard.add(List.of(saveButton));

        markup.setKeyboard(keyboard);
        return markup;
    }


    public EditMessageReplyMarkup divisionChooseField(Update update, String field){
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        //Отдаем чистую клавиатуру, или достаем из кеша
        if(divisionKeyboardCache.containsKey(userId)){
            inlineKeyboardMarkup = divisionKeyboardCache.get(userId).getReplyMarkup();
        }
        else{
            inlineKeyboardMarkup = divisionKeyboard();
        }
        if(field.equalsIgnoreCase("none")){
            for(var keyboard :inlineKeyboardMarkup.getKeyboard()){
                removeMark(keyboard.get(0));
            }
        }
        else{
            for(var keyboard: inlineKeyboardMarkup.getKeyboard()){
                //находим кнопку на которую нажали
                if(keyboard.get(0).getCallbackData().equalsIgnoreCase(field)){
                    //если маркер стоит - то снимаем
                    if(isMarked(keyboard.get(0).getCallbackData())){
                        removeMark(keyboard.get(0));
                    }
                    else{
                        //и наоборот
                        String emojiMark = ":white_check_mark:";
                        keyboard.get(0).setText(EmojiParser.parseToUnicode(emojiMark +keyboard.get(0).getText()));
                        keyboard.get(0).setCallbackData(keyboard.get(0).getCallbackData()+mark);
                    }
                }
            }
        }
        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        divisionKeyboardCache.put(userId,editedKeyboard);
        return editedKeyboard;
    }


    private void removeMark(InlineKeyboardButton button){
        button.setText(EmojiParser.parseToAliases(button.getText()).replace(":white_check_mark:",""));
        button.setCallbackData(button.getCallbackData().replace(mark,""));
    }
    private boolean isMarked(String text){
        return text.endsWith(mark);

    }

    public Set<Division> getMarkedDivisions(Long userId){
        HashSet<Division> divNames = new HashSet<>();
        EditMessageReplyMarkup editMessageReplyMarkup = divisionKeyboardCache.get(userId);
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getReplyMarkup().getKeyboard();
        for (var row: keyboard){
            if(isMarked(row.get(0).getCallbackData())){
                divNames.add(getDivisionByName(row.get(0).getCallbackData().replace(mark,"")));
            }
        }
        return divNames;
    }

    private Division getDivisionByName(String name){
        return nameToDivision.get(name);
    }

    public HashMap<String, Division> getKeyboardDivs() {
        return nameToDivision;
    }

    @Override
    public void clear(Long userId) {
        //FIXME clear
    }

}
