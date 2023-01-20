package ru.veselov.CompanyBot.cache;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.model.Department;

public interface UserDataCache extends Cache {
    BotState getUserBotState(Long id);
    void setUserBotState(Long id, BotState botState);

    void createInquiry(Long userId, Division division);
    CustomerInquiry getInquiry(Long userId);



}
