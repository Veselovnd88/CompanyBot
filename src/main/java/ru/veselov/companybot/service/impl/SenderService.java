package ru.veselov.companybot.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.ContactMessageCreator;
import ru.veselov.companybot.service.SendTask;
import ru.veselov.companybot.service.sender.InquirySender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderService {
    @Value("${bot.adminId}")
    private String adminId;
    @Value("${bot.chat-interval}")
    private long chatInterval;
    private final CompanyBot bot;
    private final ChatServiceImpl chatServiceImpl;
    private final InquirySender inquirySender;
    private final ContactMessageCreator contactMessageCreator;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private final Map<Long, LocalDateTime> chatTimers = new ConcurrentHashMap<>();
    private Chat adminChat;

    @PostConstruct
    void configure() {
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
            if (chatTimers.containsKey(chat.getId())) {
                LocalDateTime availableTimeForNextMessage = chatTimers.get(chat.getId())
                        .plus(chatInterval, ChronoUnit.MILLIS);
                SendTask sendTask = new SendTask(bot, chat, Collections.emptyList());
                if (availableTimeForNextMessage.isAfter(LocalDateTime.now())) {
                    executorService.schedule(sendTask, chatInterval, TimeUnit.MILLISECONDS);
                } else {
                    executorService.execute(sendTask);
                }
                if (chat.isChannelChat() || chat.isGroupChat()) {
                    chatTimers.put(chat.getId(), LocalDateTime.now());
                }
            }
            if (inquiry != null) {
                inquirySender.setInquiry(inquiry);
                inquirySender.send(bot, chat);
            }
            //Контакт есть ВСЕГДА, ФИО есть всегда
            contactSender.setUpContactSender(contact, inquiry != null);
            contactSender.send(bot, chat);
            if (chat.isChannelChat() || chat.isGroupChat()) {
                chatTimers.put(chat.getId(), new Date());
            }
        }
    }

    /*Метод проходит по мапе с чатами и их временем отправки, и удаляет оттуда те, в которых отправка была ранее чем
     * минуту назад*/
    private void removeOldChats() {
        List<Long> ids = chatTimers.entrySet().stream().filter(x -> (new Date(x.getValue().getTime() + chatInterval)).before(new Date()))
                .map(Map.Entry::getKey).toList();
        for (long l : ids) {
            chatTimers.remove(l);
        }
    }

}
