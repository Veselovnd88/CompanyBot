package ru.veselov.companybot.bot.handler.callback.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.handler.callback.SaveContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.event.SendCustomerDataEventPublisher;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.handler.BotExceptionToMessage;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.InquiryService;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveContactCallbackUpdateHandlerImpl implements SaveContactCallbackUpdateHandler {

    private final UserDataCacheFacade userDataCache;

    private final SendCustomerDataEventPublisher customerDataEventPublisher;

    private final CustomerService customerService;

    private final InquiryService inquiryService;

    private final KeyBoardUtils keyBoardUtils;

    private final CallbackQueryHandlerContext context;

    @PostConstruct
    public void registerInContext() {
        context.add(CallBackButtonUtils.SAVE, this);
    }

    @BotExceptionToMessage
    @Override
    public AnswerCallbackQuery processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        ContactModel contact = userDataCache.getContact(userId);
        if (checkIsContactOK(contact)) {
            InquiryModel inquiry = userDataCache.getInquiry(userId);
            customerDataEventPublisher.publishEvent(inquiry, contact);
            customerService.saveContact(contact);
            if (inquiry != null) {
                inquiryService.save(inquiry);
            }
            keyBoardUtils.clear(userId);
            userDataCache.clear(userId);
            return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                    .text(MessageUtils.SAVED).showAlert(true)
                    .build();
        } else {
            throw new ContactProcessingException(MessageUtils.NOT_ENOUGH_CONTACT, userId.toString());
        }
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE);
    }

    private boolean checkIsContactOK(ContactModel contact) {
        if (contact.getLastName() == null && contact.getFirstName() == null && contact.getSecondName() == null) {
            return false;
        }
        return contact.getEmail() != null || contact.getPhone() != null || contact.getContact() != null;
    }

}