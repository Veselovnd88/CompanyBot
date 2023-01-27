package ru.veselov.CompanyBot.cache;

import ru.veselov.CompanyBot.model.ContactModel;

public interface ContactCache extends Cache{
    void createContact(Long userId);
    ContactModel getContact(Long userId);
}
