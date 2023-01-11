package ru.veselov.CompanyBot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.util.MessageUtils;

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
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    @Getter
    private final Map<Long, Date> chatTimers = new HashMap<>();

    @Autowired
    public SenderService(CompanyBot bot, ChatService chatService, UserDataCache userDataCache, ContactCache contactCache) {
        this.bot = bot;
        this.chatService = chatService;
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
    }


    public synchronized void send(Long userId) throws TelegramApiException {//сюда должно быть передано полноценное DTO чтобы из него забирать все данные
        Map<String, SendMediaGroup> groupsCache = new HashMap<>();
        removeOldChats();
        List<Chat> allChats = chatService.findAll();
        Chat toAdmin =new Chat();
        toAdmin.setTitle("Администратору");
        toAdmin.setId(Long.valueOf(adminId));
        List<Chat> sending = new ArrayList<>(allChats);
        sending.add(toAdmin);
        for(Chat chat: sending) {
            if (chatTimers.containsKey(chat.getId())) {
                //Если в кеше с таймерами есть наш чат, то проверяем время отправки, если время + 60 секунд
                //позже текущей даты(отправка была меньше минуту назад), то запускаем эту отправку в новом треде
                //с задержкой +- 60 сек, и обновляем время отправки данного чата
                Date chatDate = new Date(chatTimers.get(chat.getId()).getTime() + chatInterval);
                if ((chatDate).after(new Date())) {
                    Thread delayedStart = new Thread(() -> {
                        try {
                            log.info("Отправлю запрос пользователя {} через {} мс", userId,
                                    chatInterval);
                            Thread.sleep(chatInterval);
                            send(userId);
                            chatTimers.put(chat.getId(), chatDate);
                        } catch (TelegramApiException e) {
                            log.error("Не удалось отправить сообщение {}", e.getMessage());
                            try {
                                bot.execute(SendMessage.builder().chatId(userId)
                                        .text(MessageUtils.ERROR).build());
                            } catch (TelegramApiException ex) {
                                log.error("Не удалось отправить сообщение об ошибке пользователя {}", userId);
                            }
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    });
                    delayedStart.start();
                    return;
                }
            }
            chatTimers.put(chat.getId(), new Date());
            CustomerInquiry inquiry = userDataCache.getInquiry(userId);
            if (inquiry != null){
                log.info("Отправляю запрос пользователя {} в канал {}", userId, chat.getTitle());
                bot.execute(SendMessage.builder().chatId(chat.getId())
                        .text("Направлен следующий запрос по тематике "+inquiry.getDepartment()).build());

                for (var message : inquiry.getMessages()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
                if (message.hasText()) {
                    bot.execute(SendMessage.builder()
                            .chatId(chat.getId())
                            .text(message.getText()).entities(message.getEntities()).build());
                }
                if (message.hasPhoto()) {
                    if (message.getMediaGroupId() == null) {
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(chat.getId());
                        sendPhoto.setCaption(message.getCaption());
                        sendPhoto.setCaptionEntities(message.getCaptionEntities());
                        sendPhoto.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
                        bot.execute(sendPhoto);
                    } else {
                        String mediaGroupId = message.getMediaGroupId();
                        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto(message.getPhoto().get(0).getFileId());
                        inputMediaPhoto.setCaption(message.getCaption());
                        inputMediaPhoto.setCaptionEntities(message.getCaptionEntities());
                        if (groupsCache.containsKey(message.getMediaGroupId())) {
                            groupsCache.get(mediaGroupId).getMedias().add(inputMediaPhoto);
                        } else {
                            SendMediaGroup sendMediaGroup = createSendMediaGroup(chat, inputMediaPhoto);
                            groupsCache.put(mediaGroupId, sendMediaGroup);
                        }
                        //Проверяем, что все посты из этой группы выбраны, и если да - отправляем группу
                        if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                            bot.execute(groupsCache.get(mediaGroupId));
                        }
                    }
                }
                if (message.hasDocument()) {
                    if (message.getMediaGroupId() == null) {
                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setChatId(chat.getId());
                        sendDocument.setCaption(message.getCaption());
                        sendDocument.setCaptionEntities(message.getCaptionEntities());
                        sendDocument.setDocument(new InputFile(message.getDocument().getFileId()));
                        bot.execute(sendDocument);

                    } else {
                        String mediaGroupId = message.getMediaGroupId();
                        InputMediaDocument inputMediaDocument = new InputMediaDocument(message.getDocument().getFileId());
                        inputMediaDocument.setCaption(message.getCaption());
                        inputMediaDocument.setCaptionEntities(message.getCaptionEntities());
                        if (groupsCache.containsKey(message.getMediaGroupId())) {
                            groupsCache.get(mediaGroupId).getMedias().add(inputMediaDocument);
                        } else {
                            SendMediaGroup sendMediaGroup = createSendMediaGroup(chat, inputMediaDocument);
                            groupsCache.put(mediaGroupId, sendMediaGroup);
                        }
                        if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                            bot.execute(groupsCache.get(mediaGroupId));
                        }
                    }
                }
                if (message.hasAudio()) {
                    if (message.getMediaGroupId() == null) {
                        SendAudio sendAudio = new SendAudio();
                        sendAudio.setChatId(chat.getId());
                        sendAudio.setCaption(message.getCaption());
                        sendAudio.setCaptionEntities(message.getCaptionEntities());
                        sendAudio.setAudio(new InputFile(message.getAudio().getFileId()));
                        bot.execute(sendAudio);
                    } else {
                        String mediaGroupId = message.getMediaGroupId();
                        InputMediaAudio inputMediaAudio = new InputMediaAudio(message.getAudio().getFileId());
                        inputMediaAudio.setCaption(message.getCaption());
                        inputMediaAudio.setCaptionEntities(message.getCaptionEntities());
                        if (groupsCache.containsKey(message.getMediaGroupId())) {
                            groupsCache.get(mediaGroupId).getMedias().add(inputMediaAudio);
                        } else {
                            SendMediaGroup sendMediaGroup = createSendMediaGroup(chat, inputMediaAudio);
                            groupsCache.put(mediaGroupId, sendMediaGroup);
                        }
                        if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                            bot.execute(groupsCache.get(mediaGroupId));
                        }
                    }
                }
                if (message.hasVideo()) {
                    if (message.getMediaGroupId() == null) {
                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setChatId(chat.getId());
                        sendVideo.setCaption(message.getCaption());
                        sendVideo.setCaptionEntities(message.getCaptionEntities());
                        sendVideo.setVideo(new InputFile(message.getVideo().getFileId()));
                        bot.execute(sendVideo);
                    } else {
                        String mediaGroupId = message.getMediaGroupId();
                        InputMediaVideo inputMediaVideo = new InputMediaVideo(message.getVideo().getFileId());
                        inputMediaVideo.setCaption(message.getCaption());
                        inputMediaVideo.setCaptionEntities(message.getCaptionEntities());
                        if (groupsCache.containsKey(message.getMediaGroupId())) {
                            groupsCache.get(mediaGroupId).getMedias().add(inputMediaVideo);
                        } else {
                            SendMediaGroup sendMediaGroup = createSendMediaGroup(chat, inputMediaVideo);
                            groupsCache.put(mediaGroupId, sendMediaGroup);
                        }
                        if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                            bot.execute(groupsCache.get(mediaGroupId));
                        }
                    }
                }
                if (message.hasAnimation()) {
                    if (message.getMediaGroupId() == null) {
                        SendAnimation sendAnimation = new SendAnimation();
                        sendAnimation.setChatId(chat.getId());
                        sendAnimation.setCaption(message.getCaption());
                        sendAnimation.setCaptionEntities(message.getCaptionEntities());
                        sendAnimation.setAnimation(new InputFile(message.getAnimation().getFileId()));
                        bot.execute(sendAnimation);
                    } else {
                        String mediaGroupId = message.getMediaGroupId();
                        InputMediaAnimation inputMediaAnimation = new InputMediaAnimation(message.getAnimation().getFileId());
                        inputMediaAnimation.setCaption(message.getCaption());
                        inputMediaAnimation.setCaptionEntities(message.getCaptionEntities());
                        if (groupsCache.containsKey(message.getMediaGroupId())) {
                            groupsCache.get(mediaGroupId).getMedias().add(inputMediaAnimation);
                        } else {
                            SendMediaGroup sendMediaGroup = createSendMediaGroup(chat, inputMediaAnimation);
                            groupsCache.put(mediaGroupId, sendMediaGroup);
                        }
                        if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                            bot.execute(groupsCache.get(mediaGroupId));
                        }
                    }
                }
            }
        }
        }
    }


    /*Функция проверяет, что все сообщения с одной медиагруппой собраны в объект SendMediaGroup
     * и что пора ее отправлять*/
    private boolean checkIfMediaGroupReadyToSend(CustomerInquiry inquiry, Message message, SendMediaGroup sendMediaGroup){
        int groupSize = inquiry.getMessages().stream().filter(x->x.getMediaGroupId()!=null)
                .map(x -> x.getMediaGroupId()
                        .equals(message.getMediaGroupId())).toList().size();
        return (groupSize==sendMediaGroup.getMedias().size());
    }
    private SendMediaGroup createSendMediaGroup(Chat chat,InputMedia inputMedia){
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chat.getId());
        List<InputMedia> list=new LinkedList<>();
        list.add(inputMedia);
        sendMediaGroup.setMedias(list);
        return sendMediaGroup;
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

}
