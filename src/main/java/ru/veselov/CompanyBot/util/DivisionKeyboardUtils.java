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
import ru.veselov.CompanyBot.entity.ManagerEntity;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;

import java.util.*;

@Component
@Slf4j
public class DivisionKeyboardUtils implements Cache {//FIXME возможно есть смысл сделать общего предка у клавиатурных классов
    private final String mark="+marked";
    private final DivisionService divisionService;
    private final HashMap<String, Division> idToDivision =new HashMap<>();
    private final HashMap<Long, InlineKeyboardMarkup> divisionKeyboardCache = new HashMap<>();
    private final ManagerService managerService;
    private final String emojiMark = ":white_check_mark:";
    @Autowired
    public DivisionKeyboardUtils(DivisionService divisionService, ManagerService managerService) {
        this.divisionService = divisionService;
        this.managerService = managerService;
    }

    public InlineKeyboardMarkup getAdminDivisionKeyboard(Long userId, Long fromId){
        List<Division> allDivisions = refreshDivisions();
        //Checking if manager exists in db, for indicating owned divisions
        Optional<ManagerEntity> oneWithDivisions = managerService.findOneWithDivisions(fromId);
        Set<Division> managersDivision=new HashSet<>();
        if(oneWithDivisions.isPresent()){
            managersDivision=oneWithDivisions.get().getDivisions();
        }
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for(var d: allDivisions){
            String name = d.getName();
            String callback = d.getDivisionId();
            if(managersDivision.contains(d)){
                name=EmojiParser.parseToUnicode(emojiMark+d.getName());
                callback=d.getDivisionId()+mark;
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(name);
            button.setCallbackData(callback);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }

        InlineKeyboardButton saveButton = new InlineKeyboardButton();
        saveButton.setCallbackData("save");
        saveButton.setText("Сохранить");
        keyboard.add(List.of(saveButton));
        markup.setKeyboard(keyboard);
        divisionKeyboardCache.put(userId, markup);
        return markup;
    }


    public InlineKeyboardMarkup getCustomerDivisionKeyboard(){
        List<Division> allDivisions = refreshDivisions();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for(var d: allDivisions){
            String name = d.getName();
            String callback = d.getDivisionId();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(name);
            button.setCallbackData(callback);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        return markup;
    }


    public EditMessageReplyMarkup divisionChooseField(Update update, String field, Long fromId){
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        //Create new keyboard or send it from our cache
        if(divisionKeyboardCache.containsKey(userId)){
            inlineKeyboardMarkup = divisionKeyboardCache.get(userId);
        }
        else{
            inlineKeyboardMarkup = getAdminDivisionKeyboard(userId,fromId);
        }
        for(var keyboard: inlineKeyboardMarkup.getKeyboard()){
                //находим кнопку на которую нажали
                if(keyboard.get(0).getCallbackData().equalsIgnoreCase(field)){
                    //если маркер стоит - то снимаем
                    if(isMarked(keyboard.get(0).getCallbackData())){
                        removeMark(keyboard.get(0));
                    }
                    else{
                        //и наоборот
                        keyboard.get(0).setText(EmojiParser.parseToUnicode(emojiMark +keyboard.get(0).getText()));
                        keyboard.get(0).setCallbackData(keyboard.get(0).getCallbackData()+mark);
                    }
                }
        }
        EditMessageReplyMarkup editedKeyboard = EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        divisionKeyboardCache.put(userId,inlineKeyboardMarkup);
        return editedKeyboard;
    }

    private void removeMark(InlineKeyboardButton button){
        button.setText(EmojiParser.parseToAliases(button.getText()).replace(emojiMark,""));
        button.setCallbackData(button.getCallbackData().replace(mark,""));
    }
    private boolean isMarked(String text){
        return text.endsWith(mark);

    }

    public Set<Division> getMarkedDivisions(Long userId){
        HashSet<Division> divNames = new HashSet<>();
        InlineKeyboardMarkup editMessageReplyMarkup = divisionKeyboardCache.get(userId);
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getKeyboard();
        for (var row: keyboard){
            if(isMarked(row.get(0).getCallbackData())){
                divNames.add(getDivisionByName(row.get(0).getCallbackData().replace(mark,"")));
            }
        }
        return divNames;
    }

    private Division getDivisionByName(String name){
        return idToDivision.get(name);
    }

    public HashMap<String, Division> getKeyboardDivs() {
        HashMap<String, Division> withMarked= new HashMap<>();
        idToDivision.forEach((x, y)->{
            withMarked.put(x,y);
            withMarked.put(x+mark,y);
                });
        return withMarked;
    }
    public HashMap<String, Division> getCachedDivisions() {
        return idToDivision;
    }

    @Override
    public void clear(Long userId) {
        divisionKeyboardCache.remove(userId);
        idToDivision.clear();
    }

    private List<Division> refreshDivisions(){
        //After creation ov keyboard all divisions placed in cache
        List<Division> allDivisions = divisionService.findAll();
        for(var d: allDivisions){
            idToDivision.put(d.getDivisionId(),d);
        }
        return allDivisions;
    }
}
