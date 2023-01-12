package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ContactMessageHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    @Autowired
    public ContactMessageHandler(UserDataCache userDataCache, ContactCache contactCache) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCache.getUserBotState(userId);

        if(update.getMessage().hasText()&&botState==BotState.AWAIT_NAME){
            String name = update.getMessage().getText();
            //TODO name.split()
            contactCache.getContact(userId).setFirstName();
        }
        if(update.getMessage().hasContact()){
            contact.setContact(update.getMessage().getContact());
        }

        return SendMessage.builder().chatId(userId).text(MessageUtils.WRONG_CONTACT_FORMAT).build();
    }

    private SendMessage saveContactMessage(Long userId) {
        InlineKeyboardButton save = new InlineKeyboardButton();
        save.setText("Сохранить");
        save.setCallbackData("save");
        InlineKeyboardButton repeat = new InlineKeyboardButton();
        repeat.setText("Повторить ввод");
        repeat.setCallbackData("repeat");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(save);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(repeat);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return SendMessage.builder().chatId(userId).text(MessageUtils.SAVE_MESSAGE)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
}
