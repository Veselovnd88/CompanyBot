package ru.veselov.companybot.bot.handler.callback.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.handler.callback.SaveContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.event.SendCustomerDataEventPublisher;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.ContactService;
import ru.veselov.companybot.service.InquiryService;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Set;

/**
 * Handle save action
 *
 * @see UserDataCacheFacade
 * @see SendCustomerDataEventPublisher
 * @see ContactService
 * @see InquiryService
 * @see CallbackQueryDataHandlerContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveContactCallbackUpdateHandlerImpl implements SaveContactCallbackUpdateHandler {

    private final UserDataCacheFacade userDataCache;

    private final SendCustomerDataEventPublisher customerDataEventPublisher;

    private final ContactService contactService;

    private final InquiryService inquiryService;

    private final ContactKeyboardHelperImpl contactKeyboardHelper;

    private final CallbackQueryDataHandlerContext context;

    @PostConstruct
    @Override
    public void registerInContext() {
        context.add(CallBackButtonUtils.SAVE, this);
    }

    /**
     * Process SAVE callback button, check if contact data is enough for saving,
     * publish event to send data to admin;
     * Save inquiry and contact data to db;
     * Clears all caches;
     *
     * @param update {@link Update} from Telegram
     * @return {@link AnswerCallbackQuery} with message that all is ok
     * @throws ContactProcessingException if not enough data for saving
     */
    @Override
    public AnswerCallbackQuery processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        ContactModel contact = userDataCache.getContact(userId);
        if (checkIsContactOK(contact)) {
            InquiryModel inquiry = userDataCache.getInquiry(userId);
            customerDataEventPublisher.publishEvent(inquiry, contact);
            contactService.saveContact(contact);
            if (inquiry != null) {
                inquiryService.save(inquiry);
            }
            contactKeyboardHelper.clear(userId);
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
