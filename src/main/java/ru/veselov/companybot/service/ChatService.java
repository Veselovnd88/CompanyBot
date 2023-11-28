package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.List;

public interface ChatService {

    void save(Chat chat);

    void remove(Long chatId);

    List<Chat> findAll();

}
