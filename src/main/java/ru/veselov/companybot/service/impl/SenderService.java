package ru.veselov.companybot.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.MessagesToSendCreator;
import ru.veselov.companybot.service.SendTask;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderService {

    @Value("${bot.adminId}")
    private String adminId;

    @Value("${bot.chat-interval}")
    private Long chatInterval;

    private final CompanyBot bot;

    private final ChatServiceImpl chatServiceImpl;

    private final ThreadPoolTaskScheduler threadPoolSenderTaskScheduler;

    private final MessagesToSendCreator messagesToSendCreator;
    private final Map<Long, Instant> chatTimers = new ConcurrentHashMap<>();

    private Chat adminChat;

    @PostConstruct
    public void configure() {
        adminChat = new Chat();
        adminChat.setId(Long.valueOf(adminId));
        adminChat.setTitle("Администратору");
    }

    public void send(InquiryModel inquiry, ContactModel contact) {
        removeOldChats();
        List<Chat> allChats = chatServiceImpl.findAll();
        List<Chat> chatsToSend = new ArrayList<>(allChats);
        chatsToSend.add(adminChat);
        for (Chat chat : chatsToSend) {
            Long chatId = chat.getId();
            log.debug("Creating and scheduling task for sending to [chat: {}]", chatId);
            List<PartialBotApiMethod<?>> messagesToSend = messagesToSendCreator
                    .createMessagesToSend(inquiry, contact, chatId.toString());
            SendTask sendTask = new SendTask(bot, chat, messagesToSend);
            if (chatTimers.containsKey(chatId)) {
                log.debug("Check if current time is after last sending + {} milliseconds", chatInterval);
                //check if last send to chat was not earlier than interval
                Instant availableTimeForNextMessage = chatTimers.get(chatId)
                        .plus(chatInterval, ChronoUnit.MILLIS);
                if (availableTimeForNextMessage.isAfter(Instant.now())) {
                    threadPoolSenderTaskScheduler
                            .schedule(sendTask, chatTimers.get(chatId).plus(chatInterval, ChronoUnit.MILLIS));
                    log.debug("Task with delay planned");
                }
            } else {
                threadPoolSenderTaskScheduler.execute(sendTask);
                log.debug("Task will be executed right now");
            }
            if (chat.isChannelChat() || chat.isGroupChat()) {
                chatTimers.compute(chatId, (key, value) ->
                        value != null ? value.plus(chatInterval, ChronoUnit.MILLIS)
                                : Instant.now().plus(chatInterval, ChronoUnit.MILLIS));
                log.debug("Put delay for [chat: {}]", chatId);
            }
        }
    }

    /**
     * Remove old chats from times cache
     */
    private void removeOldChats() {
        List<Long> chatIds = chatTimers.entrySet().stream().filter(x ->
                        x.getValue().plus(chatInterval, ChronoUnit.MILLIS).isBefore(Instant.now()))
                .map(Map.Entry::getKey).toList();
        for (long l : chatIds) {
            chatTimers.remove(l);
        }
        log.debug("Chat timers cleared");
    }

}
