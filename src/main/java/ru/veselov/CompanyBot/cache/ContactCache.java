package ru.veselov.CompanyBot.cache;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface ContactCache extends Cache{
    void addContact(Long userId,Message message);
    Message getContact(Long userId);
}
