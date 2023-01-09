package ru.veselov.CompanyBot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.cache.ContactCache;

import java.util.HashMap;

@Component
@Slf4j
public class ContactCacheImpl implements ContactCache {
    private final HashMap<Long, Message> contactCache = new HashMap<>();
    @Override
    public void clear(Long userId) {
        contactCache.remove(userId);
        log.info("Контакт пользователя {} удален из кеша", userId);
    }

    @Override
    public void addContact(Long userId, Message message) {
        contactCache.put(userId,message);
    }

    @Override
    public Message getContact(Long userId) {
        return contactCache.get(userId);
    }
}
