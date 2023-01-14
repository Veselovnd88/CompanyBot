package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
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
import ru.veselov.CompanyBot.util.KeyBoardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

@Component
@Slf4j
public class ContactCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    private final CustomerService customerService;
    private final InquiryService inquiryService;

    private final SenderService senderService;
    private final KeyBoardUtils keyBoardUtils;
    @Autowired
    public ContactCallbackHandler(UserDataCache userDataCache, ContactCache contactCache, CustomerService customerService, InquiryService inquiryService, SenderService senderService, KeyBoardUtils keyBoardUtils) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.customerService = customerService;
        this.inquiryService = inquiryService;
        this.senderService = senderService;
        this.keyBoardUtils = keyBoardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();

        switch (data){
            case "email":
                userDataCache.setUserBotState(userId,BotState.AWAIT_EMAIL);
                return keyBoardUtils.editMessageChooseField(update,"email");
            case "phone":
                userDataCache.setUserBotState(userId,BotState.AWAIT_PHONE);
                return keyBoardUtils.editMessageChooseField(update,"phone");
            case "shared":
                userDataCache.setUserBotState(userId,BotState.AWAIT_SHARED);
                return keyBoardUtils.editMessageChooseField(update,"shared");
            case "name":
                userDataCache.setUserBotState(userId,BotState.AWAIT_NAME);
                return keyBoardUtils.editMessageChooseField(update,"name");
            case "contact"://приходит из InquiryMessageHandler
            case "repeat":
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                contactCache.createContact(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .replyMarkup(keyBoardUtils.contactKeyBoard())
                        .build();
            case "save":
                customerService.saveContact(contactCache.getContact(userId));
                if(userDataCache.getInquiry(userId)!=null){
                    inquiryService.save(userDataCache.getInquiry(userId));
                }
                try {
                    senderService.send(userDataCache.getInquiry(userId),contactCache.getContact(userId));
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




}
