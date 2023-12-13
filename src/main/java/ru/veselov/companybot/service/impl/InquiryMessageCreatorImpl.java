package ru.veselov.companybot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAnimation;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.InquiryMessageCreator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InquiryMessageCreatorImpl implements InquiryMessageCreator {

    @Override
    public List<PartialBotApiMethod<?>> createBotMessagesToSend(InquiryModel inquiry, String chatId) {
        Map<String, SendMediaGroup> groupsCache = new HashMap<>();
        log.debug("Creating messaged from [customer: {}] inquiry", inquiry.getUserId());
        List<PartialBotApiMethod<?>> messagesToSend = new LinkedList<>();
        messagesToSend.add(SendMessage.builder().chatId(chatId)
                .text("Направлен следующий запрос по тематике " + inquiry.getDivision().getName()).build());
        for (var message : inquiry.getMessages()) {
            if (message.hasText()) {
                messagesToSend.add(SendMessage.builder()
                        .chatId(chatId)
                        .text(message.getText()).entities(message.getEntities()).build());
            }
            if (message.hasPhoto()) {
                if (message.getMediaGroupId() == null) {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setCaption(message.getCaption());
                    sendPhoto.setCaptionEntities(message.getCaptionEntities());
                    sendPhoto.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
                    messagesToSend.add(sendPhoto);
                } else {
                    String mediaGroupId = message.getMediaGroupId();
                    InputMediaPhoto inputMediaPhoto = new InputMediaPhoto(message.getPhoto().get(0).getFileId());
                    setUpInputMedia(inputMediaPhoto, message);
                    addMessageToMediaGroup(inputMediaPhoto, mediaGroupId, chatId, groupsCache);
                    //Проверяем, что все посты из этой группы выбраны, и если да - отправляем группу
                    if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                        messagesToSend.add(groupsCache.get(mediaGroupId));
                    }
                }
            }
            if (message.hasDocument()) {
                if (message.getMediaGroupId() == null) {
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatId);
                    sendDocument.setCaption(message.getCaption());
                    sendDocument.setCaptionEntities(message.getCaptionEntities());
                    sendDocument.setDocument(new InputFile(message.getDocument().getFileId()));
                    messagesToSend.add(sendDocument);

                } else {
                    String mediaGroupId = message.getMediaGroupId();
                    InputMediaDocument inputMediaDocument = new InputMediaDocument(message.getDocument().getFileId());
                    setUpInputMedia(inputMediaDocument, message);
                    addMessageToMediaGroup(inputMediaDocument, mediaGroupId, chatId, groupsCache);
                    if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                        messagesToSend.add(groupsCache.get(mediaGroupId));
                    }
                }
            }
            if (message.hasAudio()) {
                if (message.getMediaGroupId() == null) {
                    SendAudio sendAudio = new SendAudio();
                    sendAudio.setChatId(chatId);
                    sendAudio.setCaption(message.getCaption());
                    sendAudio.setCaptionEntities(message.getCaptionEntities());
                    sendAudio.setAudio(new InputFile(message.getAudio().getFileId()));
                    messagesToSend.add(sendAudio);
                } else {
                    String mediaGroupId = message.getMediaGroupId();
                    InputMediaAudio inputMediaAudio = new InputMediaAudio(message.getAudio().getFileId());
                    setUpInputMedia(inputMediaAudio, message);
                    addMessageToMediaGroup(inputMediaAudio, mediaGroupId, chatId, groupsCache);
                    if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                        messagesToSend.add(groupsCache.get(mediaGroupId));
                    }
                }
            }
            if (message.hasVideo()) {
                if (message.getMediaGroupId() == null) {
                    SendVideo sendVideo = new SendVideo();
                    sendVideo.setChatId(chatId);
                    sendVideo.setCaption(message.getCaption());
                    sendVideo.setCaptionEntities(message.getCaptionEntities());
                    sendVideo.setVideo(new InputFile(message.getVideo().getFileId()));
                    messagesToSend.add(sendVideo);
                } else {
                    String mediaGroupId = message.getMediaGroupId();
                    InputMediaVideo inputMediaVideo = new InputMediaVideo(message.getVideo().getFileId());
                    setUpInputMedia(inputMediaVideo, message);
                    addMessageToMediaGroup(inputMediaVideo, mediaGroupId, chatId, groupsCache);
                    if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                        messagesToSend.add(groupsCache.get(mediaGroupId));
                    }
                }
            }
            if (message.hasAnimation()) {
                if (message.getMediaGroupId() == null) {
                    SendAnimation sendAnimation = new SendAnimation();
                    sendAnimation.setChatId(chatId);
                    sendAnimation.setCaption(message.getCaption());
                    sendAnimation.setCaptionEntities(message.getCaptionEntities());
                    sendAnimation.setAnimation(new InputFile(message.getAnimation().getFileId()));
                    messagesToSend.add(sendAnimation);
                } else {
                    String mediaGroupId = message.getMediaGroupId();
                    InputMediaAnimation inputMediaAnimation = new InputMediaAnimation(message.getAnimation().getFileId());
                    setUpInputMedia(inputMediaAnimation, message);
                    addMessageToMediaGroup(inputMediaAnimation, mediaGroupId, chatId, groupsCache);
                    if (checkIfMediaGroupReadyToSend(inquiry, message, groupsCache.get(mediaGroupId))) {
                        messagesToSend.add(groupsCache.get(mediaGroupId));
                    }
                }
            }
        }
        return messagesToSend;
    }

    private static void setUpInputMedia(InputMedia inputMedia, Message message) {
        inputMedia.setCaption(message.getCaption());
        inputMedia.setCaptionEntities(message.getCaptionEntities());
    }

    private static void addMessageToMediaGroup(InputMedia inputMedia, String mediaGroupId, String chatId,
                                               Map<String, SendMediaGroup> groupsCache) {
        if (groupsCache.containsKey(mediaGroupId)) {
            groupsCache.get(mediaGroupId).getMedias().add(inputMedia);
        } else {
            SendMediaGroup sendMediaGroup = createSendMediaGroup(chatId, inputMedia);
            groupsCache.put(mediaGroupId, sendMediaGroup);
        }
    }


    private static boolean checkIfMediaGroupReadyToSend(InquiryModel inquiry, Message message, SendMediaGroup sendMediaGroup) {
        int groupSize = inquiry.getMessages().stream().filter(x -> x.getMediaGroupId() != null)
                .map(x -> x.getMediaGroupId()
                        .equals(message.getMediaGroupId())).toList().size();
        return (groupSize == sendMediaGroup.getMedias().size());
    }

    private static SendMediaGroup createSendMediaGroup(String chatId, InputMedia inputMedia) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);
        List<InputMedia> list = new LinkedList<>();
        list.add(inputMedia);
        sendMediaGroup.setMedias(list);
        return sendMediaGroup;
    }
}
