package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.service.InquiryService;
import ru.veselov.CompanyBot.service.SenderService;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ContactCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    private final CustomerService customerService;
    private final InquiryService inquiryService;

    private final SenderService senderService;
    @Autowired
    public ContactCallbackHandler(UserDataCache userDataCache, ContactCache contactCache, CustomerService customerService, InquiryService inquiryService, SenderService senderService) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.customerService = customerService;
        this.inquiryService = inquiryService;
        this.senderService = senderService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();

        switch (data){
            case "email":

            case "contact":
            case "repeat":
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .replyMarkup(contactKeyBoard())
                        .build();
            case "save":
                customerService.saveContact(userId,contactCache.getContact(userId));
                if(userDataCache.getInquiry(userId)!=null){
                    inquiryService.save(userDataCache.getInquiry(userId));
                }
                try {
                    senderService.send(userId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                //TODO отсюда будет запускаться сервис по отправке сообщений от клиентов в чат и админу
                contactCache.clear(userId);
                userDataCache.clear(userId);
                return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                        .text(MessageUtils.SAVED).build();
        }
        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR).build();
    }


    private InlineKeyboardMarkup contactKeyBoard(){
        var inputName = new InlineKeyboardButton();
        inputName.setText("Введите ФИО");
        inputName.setCallbackData("contact:name");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(inputName);
        InlineKeyboardButton inputEmail = new InlineKeyboardButton();
        inputEmail.setText("Ввести email");
        inputEmail.setCallbackData("contact:email");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(inputEmail);
        var inputPhone = new InlineKeyboardButton();
        inputPhone.setText("Ввести номер телефона");
        inputPhone.setCallbackData("contact:phone");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(inputPhone);
        var inputContact = new InlineKeyboardButton();
        inputContact.setText("Прикрепите контакт");
        inputContact.setCallbackData("contact:shared");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(inputContact);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        var markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
