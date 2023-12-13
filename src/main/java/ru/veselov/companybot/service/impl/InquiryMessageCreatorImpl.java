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
import ru.veselov.companybot.service.MediaGroupMessageHelper;
import ru.veselov.companybot.service.SimpleMessageMediaHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryMessageCreatorImpl implements InquiryMessageCreator {

    private final SimpleMessageMediaHelper simpleMessageMediaHelper;

    private final MediaGroupMessageHelper mediaGroupMessageHelper;

    /**
     * For saving order of messaging divided all messages for simple and with groups
     *
     * @param chatId  chat id for output message
     * @param inquiry {@link InquiryModel} with customer data
     * @return {@link List} with bot messages to send
     */
    @Override
    public List<PartialBotApiMethod<?>> createBotMessagesToSend(InquiryModel inquiry, String chatId) {
        log.debug("Creating messaged from [customer: {}] inquiry", inquiry.getUserId());
        List<PartialBotApiMethod<?>> messagesToSend = new LinkedList<>();
        messagesToSend.add(SendMessage.builder().chatId(chatId)
                .text("Направлен следующий запрос по тематике " + inquiry.getDivision().getName()).build());
        log.debug("Greet messages added");
        Map<Integer, Message> mediaGroupMessages = new HashMap<>();
        Map<Integer, Message> simpleMessages = new HashMap<>();
        List<Message> messages = inquiry.getMessages();
        log.debug("Creating map with messages combined in MediaGroup");
        int i = 0;
        for (var msg : messages) {
            if (msg.getMediaGroupId() != null) {
                mediaGroupMessages.put(i, msg);
            } else simpleMessages.put(i, msg);
            i++;
        }
        Map<Integer, SendMediaGroup> sendMediaGroupMessages
                = mediaGroupMessageHelper.convertMediaGroupMessages(mediaGroupMessages, chatId);
        Map<Integer, PartialBotApiMethod<?>> sendMediaMessages = simpleMessageMediaHelper
                .convertSendMediaMessage(simpleMessages, chatId);
        log.debug("Combining simple and media groups messages");
        List<? extends PartialBotApiMethod<?>> readyMessages = Stream.of(sendMediaMessages, sendMediaGroupMessages).flatMap(m -> m.entrySet().stream())
                .sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue)
                .toList();
        messagesToSend.addAll(readyMessages);
        log.debug("List contains [{}] messages to send", messagesToSend.size());
        return messagesToSend;
    }

}
