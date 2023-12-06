package ru.veselov.companybot.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.service.impl.SenderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendCustomerDataEventListener {

    private final SenderService senderService;

    @EventListener
    public void handleSensCustomerDataEvent(SendCustomerDataEvent event) {
        log.debug("Handled event for sending data to admin/chat from customer");
        try {
            senderService.send(event.getInquiry(), event.getContact());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchDivisionException e) {
            throw new RuntimeException(e);
        }
    }

}
