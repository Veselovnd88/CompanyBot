package ru.veselov.CompanyBot.cache;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.model.CustomerContact;

public interface ContactCache extends Cache{
    void createContact(Long userId);
    CustomerContact getContact(Long userId);
}
