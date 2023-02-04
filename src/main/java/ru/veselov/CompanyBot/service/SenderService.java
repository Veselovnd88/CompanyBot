package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.exception.NoSuchDivisionException;
import ru.veselov.CompanyBot.model.ContactModel;
import ru.veselov.CompanyBot.model.InquiryModel;
import ru.veselov.CompanyBot.service.sender.ContactSender;
import ru.veselov.CompanyBot.service.sender.InquirySender;

import java.util.*;

@Service
@Slf4j
public class SenderService {
    @Value("${bot.adminId}")
    private String adminId;
    @Value("${bot.chat-interval}")
    private long chatInterval;
    private final CompanyBot bot;
    private final ChatService chatService;
    private final InquirySender inquirySender;
    private final ContactSender contactSender;
    private final Map<Long, Date> chatTimers = new HashMap<>();
    private Chat adminChat;
    @Autowired
    public SenderService(CompanyBot bot, ChatService chatService, InquirySender inquirySender, ContactSender contactSender) {
        this.bot = bot;
        this.chatService = chatService;
        this.inquirySender = inquirySender;
        this.contactSender = contactSender;
    }

    public synchronized void send(InquiryModel inquiry, ContactModel contact) throws TelegramApiException, NoSuchDivisionException {
        adminChat=new Chat();
        adminChat.setId(Long.valueOf(adminId));
        adminChat.setTitle("Администратору");
        Long userId = contact.getUserId();
        removeOldChats();
        List<Chat> allChats = chatService.findAll();
        List<Chat> sending = new ArrayList<>(allChats);
        sending.add(adminChat);
        for(Chat chat: sending) {
            if (chatTimers.containsKey(chat.getId())) {
                //Если в кеше с таймерами есть наш чат, то проверяем время отправки, если время + 60 секунд
                //позже текущей даты(отправка была меньше минуту назад), то запускаем эту отправку в новом треде
                //с задержкой +- 60 сек, и обновляем время отправки данного чата
                Date chatDate = new Date(chatTimers.get(chat.getId()).getTime() + chatInterval);
                if ((chatDate).after(new Date())) {
                    Thread delayedStart = new Thread(() -> {
                        try {
                            log.info("{}: отправлю запрос пользователя через {} мс", userId,
                                    chatInterval);
                            Thread.sleep(chatInterval);
                            send(inquiry,contact);
                            chatTimers.put(chat.getId(), chatDate);
                        } catch (TelegramApiException | NoSuchDivisionException e) {
                            log.error("Не удалось отправить сообщение {}", e.getMessage());
                            bot.sendMessageWithDelay(SendMessage.builder().chatId(adminId)
                                        .text("Не удалось отправить сообщение пользователя").build());
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    });
                    delayedStart.start();
                    return;
                }
            }
            if (inquiry != null){
                inquirySender.setInquiry(inquiry);
                inquirySender.send(bot,chat);
            }
            //Контакт есть ВСЕГДА, ФИО есть всегда
            contactSender.setUpContactSender(contact,inquiry!=null);
            contactSender.send(bot,chat);
            if(chat.isChannelChat()||chat.isGroupChat()){
                chatTimers.put(chat.getId(), new Date());}
        }
    }

    /*Метод проходит по мапе с чатами и их временем отправки, и удаляет оттуда те, в которых отправка была ранее чем
     * минуту назад*/
    private void removeOldChats(){
        List<Long> ids = chatTimers.entrySet().stream().filter(x -> (new Date(x.getValue().getTime() + chatInterval)).before(new Date()))
                .map(Map.Entry::getKey).toList();
        for(long l: ids){
            chatTimers.remove(l);
        }
    }

    @Profile("test")
    public Map<Long, Date> getChatTimers(){
        return chatTimers;
    }

}
