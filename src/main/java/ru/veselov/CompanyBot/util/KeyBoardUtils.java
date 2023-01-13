package ru.veselov.CompanyBot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyBoardUtils {

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

    //FIXME определить выборку корректной команды
    public EditMessageReplyMarkup editMessageChooseField(Update update, String field){
        InlineKeyboardMarkup inlineKeyboardMarkup = contactKeyBoard();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(0);
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        inlineKeyboardButton.setText("<<"+inlineKeyboardButton.getText()+">>");
        return  EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public EditMessageReplyMarkup editMessageSavedField(Update update, String field){
        InlineKeyboardMarkup inlineKeyboardMarkup = contactKeyBoard();
        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(0);
        InlineKeyboardButton inlineKeyboardButton = buttons.get(0);
        inlineKeyboardButton.setText("OK: "+inlineKeyboardButton.getText());//FIXME заменить на смайл с галочкой
        return  EditMessageReplyMarkup.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

}
