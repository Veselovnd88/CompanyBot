package ru.veselov.companybot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAnimation;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.Sender;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InquirySender implements Sender {

    private final DivisionServiceImpl divisionService;
    private InquiryModel inquiry;
    @Autowired
    public InquirySender(DivisionServiceImpl divisionService) {
        this.divisionService = divisionService;
    }


    public void setInquiry(InquiryModel inquiry) {
        this.inquiry = inquiry;
    }

    @Override
    public void send(CompanyBot bot, Chat chat) throws TelegramApiException, NoSuchDivisionException {
        Map<String, SendMediaGroup> groupsCache = new HashMap<>();

        log.info("{}: отправляю запрос пользователя в канал {}", inquiry.getUserId(), chat.getTitle());
        //отправка сообщения с отмеченными юзерами
        bot.sendMessageWithDelay(SendMessage.builder().chatId(chat.getId())
                .text("Направлен следующий запрос по тематике "+inquiry.getDivision().getName()).build());
        for (var message : inquiry.getMessages()) {
            try {
                Thread.sleep(50);
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
        inquiry=null;
    }

    private boolean checkIfMediaGroupReadyToSend(InquiryModel inquiry, Message message, SendMediaGroup sendMediaGroup){
        int groupSize = inquiry.getMessages().stream().filter(x->x.getMediaGroupId()!=null)
                .map(x -> x.getMediaGroupId()
                        .equals(message.getMediaGroupId())).toList().size();
        return (groupSize==sendMediaGroup.getMedias().size());
    }
    private SendMediaGroup createSendMediaGroup(Chat chat, InputMedia inputMedia){
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chat.getId());
        List<InputMedia> list=new LinkedList<>();
        list.add(inputMedia);
        sendMediaGroup.setMedias(list);
        return sendMediaGroup;
    }

}
