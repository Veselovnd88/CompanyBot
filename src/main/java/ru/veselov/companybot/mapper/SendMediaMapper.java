package ru.veselov.companybot.mapper;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface SendMediaMapper {

    SendPhoto toSendPhoto(Message message, String chatId);

    SendDocument toSendDocument(Message message, String chatId);

    SendAudio toSendAudio(Message message, String chatId);

    SendVideo toSendVideo(Message message, String chatId);

    SendAnimation toSendAnimation(Message message, String chatId);

    SendVoice toSendVoice(Message message, String chatId);

}
