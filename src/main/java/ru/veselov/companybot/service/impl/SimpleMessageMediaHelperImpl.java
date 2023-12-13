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

    private final SendMediaMapper sendMediaMapper;

    @Override
    public Map<Integer, PartialBotApiMethod<?>> convertSendMediaMessage(Map<Integer, Message> messages, String chatId) {
        Map<Integer, PartialBotApiMethod<?>> messagesToSend = new LinkedHashMap<>();
        for (var entry : messages.entrySet()) {
            Message message = entry.getValue();
            Integer position = entry.getKey();
            if (message.hasText()) {
                log.debug("Text message added to list for sending");
                messagesToSend.put(position, SendMessage.builder()
                        .chatId(chatId)
                        .text(message.getText()).entities(message.getEntities()).build());
            }
            if (message.hasPhoto()) {
                SendPhoto sendPhoto = sendMediaMapper.toSendPhoto(message, chatId);
                messagesToSend.put(position, sendPhoto);
            }
            if (message.hasDocument()) {
                SendDocument sendDocument = sendMediaMapper.toSendDocument(message, chatId);
                messagesToSend.put(position, sendDocument);
            }
            if (message.hasAudio()) {
                SendAudio sendAudio = sendMediaMapper.toSendAudio(message, chatId);
                messagesToSend.put(position, sendAudio);
            }
            if (message.hasVideo()) {
                SendVideo sendVideo = sendMediaMapper.toSendVideo(message, chatId);
                messagesToSend.put(position, sendVideo);
            }
            if (message.hasAnimation()) {
                SendAnimation sendAnimation = sendMediaMapper.toSendAnimation(message, chatId);
                messagesToSend.put(position, sendAnimation);
            }
        }
        return messagesToSend;
    }
}

