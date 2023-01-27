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
import ru.veselov.CompanyBot.exception.NoDivisionKeyboardException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.exception.NoSuchManagerException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;

import java.util.*;

@Component
@Slf4j
public class DivisionKeyboardUtils implements Cache {//FIXME возможно есть смысл сделать общего предка у клавиатурных классов
    private final String mark="+marked";
    private final String emojiMark = ":white_check_mark:";
    private final HashMap<String, DivisionModel> idToDivision =new HashMap<>();
    private final HashMap<Long, InlineKeyboardMarkup> divisionKeyboardCache = new HashMap<>();

    private final DivisionService divisionService;
    private final ManagerService managerService;
    @Autowired
    public DivisionKeyboardUtils(DivisionService divisionService, ManagerService managerService) {
        this.divisionService = divisionService;
        this.managerService = managerService;
    }

    public InlineKeyboardMarkup getAdminDivisionKeyboard(Long userId, Long fromId) throws NoDivisionsException {
        List<DivisionModel> allDivisions = refreshDivisions();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        //Checking if manager exists in db, for indicating owned divisions
        ManagerModel oneWithDivisions = null;
        try {
            oneWithDivisions = managerService.findOneWithDivisions(fromId);
        } catch (NoSuchManagerException ignored) {
        }


        for(var d: allDivisions){
            String name = d.getName();
            String callback = d.getDivisionId();
            if(oneWithDivisions!=null){
                Set<DivisionModel> divisions = oneWithDivisions.getDivisions();
                if(divisions.contains(d)){
                    name=EmojiParser.parseToUnicode(emojiMark+d.getName());
                    callback=d.getDivisionId()+mark;
                }}
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


    public InlineKeyboardMarkup getCustomerDivisionKeyboard() throws NoDivisionsException {
        List<DivisionModel> allDivisions = refreshDivisions();
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


    public EditMessageReplyMarkup divisionChooseField(Update update, String field) throws NoDivisionsException, NoDivisionKeyboardException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        InlineKeyboardMarkup inlineKeyboardMarkup;
        //Create new keyboard or send it from our cache
        if(divisionKeyboardCache.containsKey(userId)){
            inlineKeyboardMarkup = divisionKeyboardCache.get(userId);
        }
        else{
            throw new NoDivisionKeyboardException();
        }
        for(var keyboard: inlineKeyboardMarkup.getKeyboard()){
                //finding pressed button, but we don't need to mark save button
                if(keyboard.get(0).getCallbackData().equalsIgnoreCase(field)
                &&!keyboard.get(0).getCallbackData().equalsIgnoreCase("save")){
                    if(isMarked(keyboard.get(0).getCallbackData())){
                        removeMark(keyboard.get(0));
                    }
                    else{
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




    public Set<DivisionModel> getMarkedDivisions(Long userId){
        HashSet<DivisionModel> divNames = new HashSet<>();
        InlineKeyboardMarkup editMessageReplyMarkup = divisionKeyboardCache.get(userId);
        List<List<InlineKeyboardButton>> keyboard = editMessageReplyMarkup.getKeyboard();
        for (var row: keyboard){
            if(isMarked(row.get(0).getCallbackData())){
                divNames.add(getDivisionByName(row.get(0).getCallbackData().replace(mark,"")));
            }
        }
        return divNames;
    }
    public HashMap<String, DivisionModel> getMapKeyboardDivisions() throws NoDivisionsException {
        if(idToDivision.isEmpty()){
            refreshDivisions();
        }
        HashMap<String, DivisionModel> withMarked= new HashMap<>();
        idToDivision.forEach((x, y)->{
            withMarked.put(x,y);
            withMarked.put(x+mark,y);
                });
        return withMarked;
    }
    public Map<String, DivisionModel> getCachedDivisions() throws NoDivisionsException {
        if(idToDivision.isEmpty()){
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

    private List<DivisionModel> refreshDivisions() throws NoDivisionsException {
        //After creation ov keyboard all divisions placed in cache
        List<DivisionModel> allDivisions = divisionService.findAll();
        if(allDivisions.isEmpty()){
            throw new NoDivisionsException();
        }
        for(var d: allDivisions){
            idToDivision.put(d.getDivisionId(),d);
        }
        return allDivisions;
    }
    private DivisionModel getDivisionByName(String name){
        return idToDivision.get(name);
    }
    private void removeMark(InlineKeyboardButton button){
        button.setText(EmojiParser.parseToAliases(button.getText()).replace(emojiMark,""));
        button.setCallbackData(button.getCallbackData().replace(mark,""));
    }
    private boolean isMarked(String text){
        return text.endsWith(mark);
    }
}
