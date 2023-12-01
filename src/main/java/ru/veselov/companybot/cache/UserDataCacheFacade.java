package ru.veselov.companybot.cache;

import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

public interface UserDataCacheFacade extends Cache {

    BotState getUserBotState(Long id);

    void setUserBotState(Long id, BotState botState);

    void createInquiry(Long userId, DivisionModel division);

    InquiryModel getInquiry(Long userId);

    void createContact(Long userId);

    ContactModel getContact(Long userId);

}
