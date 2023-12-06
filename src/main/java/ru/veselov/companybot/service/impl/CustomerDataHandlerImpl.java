package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.event.SendCustomerDataEventPublisher;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.CustomerDataHandler;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.InquiryService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDataHandlerImpl implements CustomerDataHandler {

    private final UserDataCacheFacade userDataCache;

    private final SendCustomerDataEventPublisher customerDataEventPublisher;

    private final CustomerService customerService;

    private final InquiryService inquiryService;

    private final KeyBoardUtils keyBoardUtils;

    @Override
    public void handle(Long userId) {
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

        } else {
            throw new ContactProcessingException(MessageUtils.NOT_ENOUGH_CONTACT, userId.toString());
        }
    }


    private boolean checkIsContactOK(ContactModel contact) {
        if (contact.getLastName() == null && contact.getFirstName() == null && contact.getSecondName() == null) {
            return false;
        }
        return contact.getEmail() != null || contact.getPhone() != null || contact.getContact() != null;
    }
}
