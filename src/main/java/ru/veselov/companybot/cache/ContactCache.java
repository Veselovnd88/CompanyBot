package ru.veselov.companybot.cache;

import ru.veselov.companybot.model.ContactModel;

public interface ContactCache extends Clearable {
    void createContact(Long userId);
    ContactModel getContact(Long userId);
}
