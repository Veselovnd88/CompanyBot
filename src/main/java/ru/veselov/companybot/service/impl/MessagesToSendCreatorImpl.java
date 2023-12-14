package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.ContactMessageCreator;
import ru.veselov.companybot.service.InquiryMessageCreator;
import ru.veselov.companybot.service.MessagesToSendCreator;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessagesToSendCreatorImpl implements MessagesToSendCreator {

    private final InquiryMessageCreator inquiryMessageCreator;

    private final ContactMessageCreator contactMessageCreator;

    @Override
    public List<PartialBotApiMethod<?>> createMessagesToSend(InquiryModel inquiry, ContactModel contact, String chatId) {
        Boolean hasInquiry = inquiry != null;
        List<PartialBotApiMethod<?>> inquiryMessages = new LinkedList<>();
        if (Boolean.TRUE.equals(hasInquiry)) {
            inquiryMessages = inquiryMessageCreator.createBotMessagesToSend(inquiry, chatId);
        }
        List<BotApiMethod<?>> contactMessages = contactMessageCreator
                .createBotMessagesToSend(contact, chatId, hasInquiry);
        inquiryMessages.addAll(contactMessages);
        return inquiryMessages;
    }

}
