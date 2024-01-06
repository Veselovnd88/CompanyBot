package ru.veselov.companybot.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface CompanyInfoService {

    Message save(Message message);

    Message getLast();

}
