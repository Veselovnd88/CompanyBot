package ru.veselov.companybot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.model.ContactModel;

import java.util.HashMap;

@Component
@Slf4j
public class ContactCacheImpl implements ContactCache {
    private final HashMap<Long, ContactModel> contactCache = new HashMap<>();
    @Override
    public void clear(Long userId) {
        contactCache.remove(userId);
        log.info("{}: контакт пользователя удален из кеша", userId);
    }

    @Override
    public void createContact(Long userId) {
        log.info("{}: создан объект Contact для пользователя", userId);
        ContactModel contactModel = new ContactModel();
        contactModel.setUserId(userId);
        contactCache.put(userId, contactModel);
    }

    @Override
    public ContactModel getContact(Long userId) {
        return contactCache.get(userId);
    }
}
