package ru.veselov.companybot.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.InquiryService;
import ru.veselov.companybot.service.impl.SenderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendCustomerDataEventListener {

    private final SenderService senderService;

    private final CustomerService customerService;

    private final InquiryService inquiryService;

    @Async
    @EventListener
    public void handleSendCustomerDataEvent(SendCustomerDataEvent event) {
        log.debug("Handled event for sending data to admin/chat from customer");
        InquiryModel inquiry = event.getInquiry();
        ContactModel contact = event.getContact();
        try {
            senderService.send(inquiry, contact);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchDivisionException e) {
            throw new RuntimeException(e);
        }
    }

}
