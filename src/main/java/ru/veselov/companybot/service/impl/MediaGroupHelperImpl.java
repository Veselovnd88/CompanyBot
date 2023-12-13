package ru.veselov.companybot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAnimation;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import ru.veselov.companybot.service.MediaGroupHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MediaGroupHelperImpl implements MediaGroupHelper {
    /**
     * Convert passed messages that should be in media groups with order;
     * <p>
     * Aggregate content of messages by their media group in cache;
     * In parallel save their positions in original queue
     * Then check if we have duplicate SendMediaGroups and collect to final Map with positions
     * </p>
     *
     * @param mediaGroupMessages {@link Map} messages in their original order
     * @param chatId             chat id for assigning to {@link SendMediaGroup}
     * @return {@link Map} sendMediaGroups in original order without duplicates
     */
    @Override
    public Map<Integer, SendMediaGroup> convertMediaGroupMessages(Map<Integer, Message> mediaGroupMessages,
                                                                  String chatId) {
        Map<String, SendMediaGroup> mediaIdByGroupMap = new LinkedHashMap<>();
        Map<Integer, SendMediaGroup> messagesToSend = new HashMap<>();
        Map<String, List<Integer>> tempListForOrderOfGroups = new HashMap<>();
        for (var entry : mediaGroupMessages.entrySet()) {
            Message message = entry.getValue();
            String mediaGroupId = message.getMediaGroupId();
            tempListForOrderOfGroups.computeIfAbsent(mediaGroupId, k -> {
                LinkedList<Integer> positions = new LinkedList<>();
                positions.add(entry.getKey());
                return positions;
            });
            if (message.hasPhoto()) {
                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto(message.getPhoto().get(0).getFileId());
                setUpInputMedia(inputMediaPhoto, message);
                addMessageToMediaGroup(inputMediaPhoto, mediaGroupId, chatId, mediaIdByGroupMap);
            }
            if (message.hasDocument()) {
                InputMediaDocument inputMediaDocument = new InputMediaDocument(message.getDocument().getFileId());
                setUpInputMedia(inputMediaDocument, message);
                addMessageToMediaGroup(inputMediaDocument, mediaGroupId, chatId, mediaIdByGroupMap);
            }
            if (message.hasAudio()) {
                InputMediaAudio inputMediaAudio = new InputMediaAudio(message.getAudio().getFileId());
                setUpInputMedia(inputMediaAudio, message);
                addMessageToMediaGroup(inputMediaAudio, mediaGroupId, chatId, mediaIdByGroupMap);
            }
            if (message.hasAnimation()) {
                InputMediaAnimation inputMediaAnimation = new InputMediaAnimation(message.getAnimation().getFileId());
                setUpInputMedia(inputMediaAnimation, message);
                addMessageToMediaGroup(inputMediaAnimation, mediaGroupId, chatId, mediaIdByGroupMap);
            }
            if (message.hasVideo()) {
                InputMediaVideo inputMediaVideo = new InputMediaVideo(message.getVideo().getFileId());
                setUpInputMedia(inputMediaVideo, message);
                addMessageToMediaGroup(inputMediaVideo, mediaGroupId, chatId, mediaIdByGroupMap);
            }
        }
        for (var entry : tempListForOrderOfGroups.entrySet()) {
            Integer position = entry.getValue().get(0);
            String mediaGroupId = entry.getKey();
            messagesToSend.put(position, mediaIdByGroupMap.get(mediaGroupId));
        }
        return messagesToSend;
    }

    /**
     * Set up common fields for all {@link InputMedia}
     */
    private static void setUpInputMedia(InputMedia inputMedia, Message message) {
        inputMedia.setCaption(message.getCaption());
        inputMedia.setCaptionEntities(message.getCaptionEntities());
    }

    /**
     * Add SendMediaGroupMessage to temporary map, and add input media to it
     */
    private void addMessageToMediaGroup(InputMedia inputMedia, String mediaGroupId, String chatId, Map<String,
            SendMediaGroup> mediaIdByGroupMap) {
        if (mediaIdByGroupMap.containsKey(mediaGroupId)) {
            mediaIdByGroupMap.get(mediaGroupId).getMedias().add(inputMedia);
        } else {
            SendMediaGroup sendMediaGroup = createSendMediaGroup(chatId, inputMedia);
            mediaIdByGroupMap.put(mediaGroupId, sendMediaGroup);
        }
    }

    /**
     * Just create SendMediaGroup with 1st input media
     */
    private SendMediaGroup createSendMediaGroup(String chatId, InputMedia inputMedia) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);
        List<InputMedia> list = new LinkedList<>();
        list.add(inputMedia);
        sendMediaGroup.setMedias(list);
        return sendMediaGroup;
    }

}
