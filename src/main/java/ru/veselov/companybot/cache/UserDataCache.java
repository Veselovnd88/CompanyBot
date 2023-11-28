package ru.veselov.companybot.cache;

import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

public interface UserDataCache extends Cache {
    BotState getUserBotState(Long id);
    void setUserBotState(Long id, BotState botState);

    void createInquiry(Long userId, DivisionModel division);
    InquiryModel getInquiry(Long userId);



}
