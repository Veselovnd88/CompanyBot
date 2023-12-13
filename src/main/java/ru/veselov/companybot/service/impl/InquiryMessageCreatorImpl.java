package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.InquiryMessageCreator;
import ru.veselov.companybot.service.MediaGroupHelper;
import ru.veselov.companybot.service.SimpleMessageMediaHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryMessageCreatorImpl implements InquiryMessageCreator {

    private final SimpleMessageMediaHelper simpleMessageMediaHelper;

    private final MediaGroupHelper mediaGroupHelper;

    /**
     * Don't want to destroy order of messages
     */
    @Override
    public List<PartialBotApiMethod<?>> createBotMessagesToSend(InquiryModel inquiry, String chatId) {
        Map<String, SendMediaGroup> groupsCache = new HashMap<>();
        log.debug("Creating messaged from [customer: {}] inquiry", inquiry.getUserId());
        List<PartialBotApiMethod<?>> messagesToSend = new LinkedList<>();
        messagesToSend.add(SendMessage.builder().chatId(chatId)
                .text("Направлен следующий запрос по тематике " + inquiry.getDivision().getName()).build());
        Map<Integer, Message> mediaGroupMessages = new HashMap<>();
        Map<Integer, Message> simpleMessages = new HashMap<>();
        List<Message> messages = inquiry.getMessages();
        int i = 0;
        for (var msg : messages) {
            if (msg.getMediaGroupId() != null) {
                mediaGroupMessages.put(i, msg);
            } else simpleMessages.put(i, msg);
            i++;
        }

        Map<Integer, SendMediaGroup> sendMediaGroupMessages
                = mediaGroupHelper.convertMediaGroupMessages(mediaGroupMessages, chatId);

        Map<Integer, PartialBotApiMethod<?>> sendMediaMessages = simpleMessageMediaHelper
                .convertSendMediaMessage(simpleMessages, chatId);

        return messagesToSend;
    }


}
