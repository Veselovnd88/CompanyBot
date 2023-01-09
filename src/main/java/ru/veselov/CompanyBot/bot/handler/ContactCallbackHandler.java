package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.service.InquiryService;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class ContactCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    private final CustomerService customerService;
    private final InquiryService inquiryService;
    @Autowired
    public ContactCallbackHandler(UserDataCache userDataCache, ContactCache contactCache, CustomerService customerService, InquiryService inquiryService) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.customerService = customerService;
        this.inquiryService = inquiryService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();

        switch (data){
            case "contact":
            case "repeat":
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .build();
            case "save":
                customerService.saveContact(userId,contactCache.getContact(userId));
                if(userDataCache.getInquiry(userId)!=null){
                    inquiryService.save(userDataCache.getInquiry(userId));
                }
                contactCache.clear(userId);
                userDataCache.clear(userId);
                //TODO отсюда будет запускаться сервис по отправке сообщений от клиентов в чат и админу
                return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                        .text(MessageUtils.SAVED).build();
        }
        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR).build();
    }
}
