package ru.veselov.companybot.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.mapper.SendMediaMapper;

@Component
@Slf4j
public class SendMediaMapperImpl implements SendMediaMapper {

    @Override
    public SendPhoto toSendPhoto(Message message, String chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(message.getCaption());
        sendPhoto.setCaptionEntities(message.getCaptionEntities());
        sendPhoto.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
        return sendPhoto;
    }

    @Override
    public SendDocument toSendDocument(Message message, String chatId) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setCaption(message.getCaption());
        sendDocument.setCaptionEntities(message.getCaptionEntities());
        sendDocument.setDocument(new InputFile(message.getDocument().getFileId()));
        return sendDocument;
    }

    @Override
    public SendAudio toSendAudio(Message message, String chatId) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setCaption(message.getCaption());
        sendAudio.setCaptionEntities(message.getCaptionEntities());
        sendAudio.setAudio(new InputFile(message.getAudio().getFileId()));
        return sendAudio;
    }

    @Override
    public SendVideo toSendVideo(Message message, String chatId) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setCaption(message.getCaption());
        sendVideo.setCaptionEntities(message.getCaptionEntities());
        sendVideo.setVideo(new InputFile(message.getVideo().getFileId()));
        return sendVideo;
    }

    @Override
    public SendAnimation toSendAnimation(Message message, String chatId) {
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(chatId);
        sendAnimation.setCaption(message.getCaption());
        sendAnimation.setCaptionEntities(message.getCaptionEntities());
        sendAnimation.setAnimation(new InputFile(message.getAnimation().getFileId()));
        return sendAnimation;
    }

    @Override
    public SendVoice toSendVoice(Message message, String chatId) {
        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(chatId);
        sendVoice.setCaption(message.getCaption());
        sendVoice.setCaptionEntities(message.getCaptionEntities());
        sendVoice.setVoice(new InputFile(message.getVoice().getFileId()));
        return sendVoice;
    }
}
