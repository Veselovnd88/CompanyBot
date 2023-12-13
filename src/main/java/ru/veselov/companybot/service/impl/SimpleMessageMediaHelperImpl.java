package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.mapper.SendMediaMapper;
import ru.veselov.companybot.service.SimpleMessageMediaHelper;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleMessageMediaHelperImpl implements SimpleMessageMediaHelper {

    public static final String MESSAGE_ADDED = "[{}] message, [pos: {}] added to list for sending";
    private final SendMediaMapper sendMediaMapper;

    @Override
    public Map<Integer, PartialBotApiMethod<?>> convertSendMediaMessage(Map<Integer, Message> messages, String chatId) {
        log.debug("Start to convert simple messages to output bot messages");
        Map<Integer, PartialBotApiMethod<?>> messagesToSend = new LinkedHashMap<>();
        for (var entry : messages.entrySet()) {
            Message message = entry.getValue();
            Integer position = entry.getKey();
            if (message.hasText()) {
                messagesToSend.put(position, SendMessage.builder()
                        .chatId(chatId)
                        .text(message.getText()).entities(message.getEntities()).build());
                log.debug(MESSAGE_ADDED, "Text:", position);
            }
            if (message.hasPhoto()) {
                SendPhoto sendPhoto = sendMediaMapper.toSendPhoto(message, chatId);
                messagesToSend.put(position, sendPhoto);
                log.debug(MESSAGE_ADDED, sendPhoto.getClass().getSimpleName(), position);
            }
            if (message.hasDocument()) {
                SendDocument sendDocument = sendMediaMapper.toSendDocument(message, chatId);
                messagesToSend.put(position, sendDocument);
                log.debug(MESSAGE_ADDED, sendDocument.getClass().getSimpleName(), position);
            }
            if (message.hasAudio()) {
                SendAudio sendAudio = sendMediaMapper.toSendAudio(message, chatId);
                messagesToSend.put(position, sendAudio);
                log.debug(MESSAGE_ADDED, sendAudio.getClass().getSimpleName(), position);
            }
            if (message.hasVideo()) {
                SendVideo sendVideo = sendMediaMapper.toSendVideo(message, chatId);
                messagesToSend.put(position, sendVideo);
                log.debug(MESSAGE_ADDED, sendVideo.getClass().getSimpleName(), position);
            }
            if (message.hasAnimation()) {
                SendAnimation sendAnimation = sendMediaMapper.toSendAnimation(message, chatId);
                messagesToSend.put(position, sendAnimation);
                log.debug(MESSAGE_ADDED, sendAnimation.getClass().getSimpleName(), position);
            }
        }
        log.debug("List contains {} simple messages with content", messagesToSend.size());
        return messagesToSend;
    }
}

