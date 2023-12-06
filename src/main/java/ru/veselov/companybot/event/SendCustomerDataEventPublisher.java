package ru.veselov.companybot.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;

/**
 * Publishes event for sending customer data to responsible user
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SendCustomerDataEventPublisher {

    private final ApplicationEventPublisher publisher;

    /**
     * Create event and publish
     *
     * @param inquiry {@link InquiryModel} inquiry to send
     * @param contact {@link ContactModel} contact to send
     */
    public void publishEvent(InquiryModel inquiry, ContactModel contact) {
        SendCustomerDataEvent customerDataEvent = new SendCustomerDataEvent(inquiry, contact);
        publisher.publishEvent(customerDataEvent);
    }

}
