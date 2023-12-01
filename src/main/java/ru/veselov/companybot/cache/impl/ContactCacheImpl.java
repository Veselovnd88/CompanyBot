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
        log.debug("Contact for [user id: {}] removed from cache", userId);
    }

    @Override
    public void createContact(Long userId) {
        ContactModel contactModel = new ContactModel();
        contactModel.setUserId(userId);
        contactCache.put(userId, contactModel);
        log.debug("Contact created for [user id: {}]", userId);
    }

    @Override
    public ContactModel getContact(Long userId) {
        return contactCache.get(userId);
    }
}
