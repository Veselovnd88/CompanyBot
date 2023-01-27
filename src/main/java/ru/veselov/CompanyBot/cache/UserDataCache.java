package ru.veselov.CompanyBot.cache;

import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.InquiryModel;

public interface UserDataCache extends Cache {
    BotState getUserBotState(Long id);
    void setUserBotState(Long id, BotState botState);

    void createInquiry(Long userId, DivisionModel division);
    InquiryModel getInquiry(Long userId);



}
