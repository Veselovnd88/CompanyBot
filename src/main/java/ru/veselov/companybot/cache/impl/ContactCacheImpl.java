package ru.veselov.companybot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.model.ContactModel;

import java.util.HashMap;

/**
 * Cache for storing temporary contact data of user(customer) in {@link ContactModel},
 * customer will fill it in several steps/commands
 */
@Component
@Slf4j
public class ContactCacheImpl implements ContactCache {

    private final HashMap<Long, ContactModel> contactCache = new HashMap<>();

    /**
     * Clear data for passed userId
     */
    @Override
    public void clear(Long userId) {
        contactCache.remove(userId);
        log.debug("Contact for [user id: {}] removed from cache", userId);
    }

    /**
     * Create new {@link ContactModel} for passed userId, place it in map
     */
    @Override
    public void createContact(Long userId) {
        ContactModel contactModel = ContactModel.builder().userId(userId).build();
        contactCache.put(userId, contactModel);
        log.debug("Contact created for [user id: {}]", userId);
    }

    /**
     * Get {@link ContactModel} by specified userId
     *
     * @return {@link ContactModel}
     */
    @Override
    public ContactModel getContact(Long userId) {
        return contactCache.get(userId);
    }
}
